package com.fossgalaxy.games.fireworks.ai;

import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.state.*;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.events.CardDrawn;
import com.fossgalaxy.games.fireworks.state.events.CardReceived;
import com.fossgalaxy.games.fireworks.state.events.GameEvent;
import com.fossgalaxy.games.fireworks.state.events.GameInformation;
import com.fossgalaxy.games.fireworks.utils.DebugUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * A basic runner for the game of Hanabi.
 */
public class GameRunner {
    private static final int RULE_STRIKES = 1; //how many times can a player return an illegal move before we give up?
    private static final int[] HAND_SIZE = {-1, -1, 5, 5, 4, 4};
    private final Logger logger = LoggerFactory.getLogger(GameRunner.class);
    private final String gameID;
    protected final Player[] players;
    protected final GameState state;

    private int nPlayers;
    private int moves;

    private int nextPlayer;

    /**
     * Create a game runner with a given ID and a number of players.
     *
     * @param id           the Id of the game
     * @param playersCount the number of players that will be playing
     * @deprecated use string IDs instead
     */
    @Deprecated
    public GameRunner(UUID id, int playersCount) {
        this(id.toString(), playersCount);
    }

    /**
     * Create a game runner with a given ID and number of players.
     *
     * @param gameID          the ID of the game
     * @param expectedPlayers the number of players we expect to be playing.
     */
    public GameRunner(String gameID, int expectedPlayers) {
        assert expectedPlayers >= 2 : "too few players";
        assert expectedPlayers < HAND_SIZE.length : "too many players";

        this.players = new Player[expectedPlayers];
        this.state = new BasicState(HAND_SIZE[expectedPlayers], expectedPlayers);
        this.nPlayers = 0;

        this.nextPlayer = 0;
        this.moves = 0;
        this.gameID = gameID;
    }

    /**
     * Add a player to the game.
     * <p>
     * This should not be attempted once the game has started.
     *
     * @param player the player to add to the game
     */
    public void addPlayer(Player player) {
        logger.info("player {} is {}", nPlayers, player);
        players[nPlayers++] = Objects.requireNonNull(player);
    }

    /**
     * Initialise the game for the players.
     * <p>
     * This method does the setup phase for the game.
     * <p>
     * this method is responsible for:
     * 1) telling player their IDs
     * 2) initialising the game state and deck order
     * 3) informing players about the number of players and starting resource values
     * 4) dealing and declaring the values in the player's initial hands.
     * <p>
     * You should <b>not</b> call this method directly - calling playGame calls it for you on your behalf!
     *
     * @param seed the random seed to use for deck ordering.
     */
    protected void init(Long seed) {
        logger.info("game init started - {} player game with seed {}", players.length, seed);
        long startTime = getTick();

        //step 1: tell all players their IDs
        for (int i = 0; i < players.length; i++) {
            logger.info("player {} is {}", i, players[i]);
            players[i].setID(i, players.length);
        }

        state.init(seed);

        //let the players know the game has started and the starting state
        send(new GameInformation(nPlayers, HAND_SIZE[nPlayers], state.getInfomation(), state.getLives()));

        //tell players about their hands
        for (int player = 0; player < players.length; player++) {
            Hand hand = state.getHand(player);

            for (int slot = 0; slot < hand.getSize(); slot++) {
                Card cardInSlot = hand.getCard(slot);
                send(new CardDrawn(player, slot, cardInSlot.colour, cardInSlot.value));
                send(new CardReceived(player, slot, state.getDeck().hasCardsLeft()));
            }
        }

        long endTime = getTick();
        logger.info("Game init complete: took {} ms", endTime - startTime);
    }


    //TODO find a better way of doing this logging.
    protected void writeState(GameState state) {
        DebugUtils.printState(logger, state);
    }

    private long getTick() {
        return System.currentTimeMillis();
    }

    //TODO time limit the agent

    /**
     * Ask the next player for their move.
     */
    protected void nextMove() {
        Player player = players[nextPlayer];
        assert player != null : "that player is not valid";

        logger.debug("asking player {} for their move", nextPlayer);
        long startTime = getTick();

        //get the action and try to apply it
        Action action = player.getAction();

        long endTime = getTick();
        logger.debug("agent {} took {} ms to make their move", nextPlayer, endTime - startTime);
        logger.debug("move {}: player {} made move {}", moves, nextPlayer, action);

        //if the more was illegal, throw a rules violation
        if (!action.isLegal(nextPlayer, state)) {
            throw new RulesViolation(action);
        }

        //perform the action and get the effects
        logger.info("player {} made move {} as turn {}", nextPlayer, action, moves);
        moves++;
        Collection<GameEvent> events = action.apply(nextPlayer, state);
        events.forEach(this::send);

        //make sure it's the next player's turn
        nextPlayer = (nextPlayer + 1) % players.length;
    }

    /**
     * Play the game and generate the outcome.
     * <p>
     * This will play the game and generate a result.
     *
     * @param seed the seed to use for deck ordering
     * @return the result of the game
     */
    public GameStats playGame(Long seed) {
        int strikes = 0;

        try {
            assert nPlayers == players.length;
            init(seed);

            while (!state.isGameOver()) {
                try {
                    state.tick();
                    writeState(state);
                    nextMove();
                } catch (RulesViolation rv) {
                    logger.warn("got rules violation when processing move", rv);
                    strikes++;

                    //If we're not being permissive, end the game.
                    if (strikes <= RULE_STRIKES) {
                        logger.error("Maximum strikes reached, ending game");
                        break;
                    }
                }
            }
            if(state.getLives()==0) {
                return new GameStats(gameID, players.length, 0, state.getLives(), moves, state.getInfomation(), strikes);
            }
            else {
            	return new GameStats(gameID, players.length, state.getScore(), state.getLives(), moves, state.getInfomation(), strikes);
            }
        } catch (Exception ex) {
            logger.error("the game went bang", ex);
            return new GameStats(gameID, players.length, state.getScore(), state.getLives(), moves, state.getInfomation(), 1);
        }

    }

    //send messages as soon as they are available
    protected void send(GameEvent event) {
        logger.debug("game sent event: {}", event);
        for (int i = 0; i < players.length; i++) {
            if (event.isVisibleTo(i)) {
                players[i].sendMessage(event);
            }
        }
    }

}