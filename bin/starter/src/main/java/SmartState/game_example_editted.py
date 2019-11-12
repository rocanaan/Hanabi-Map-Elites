# Copyright 2018 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Example code demonstrating the Python Hanabi interface."""

from __future__ import print_function

import numpy as np
import pyhanabi

import socket, errno, time


HOST = "localhost"
PORT = 9999
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print('Socket created')
try:
  s.bind((HOST, PORT))
except socket.error as err:
  print('Bind failed. Error Code : ' .format(err))
s.listen(1000)
print("Socket Listening")
conn, addr = s.accept()

message = ""


def run_game(game_parameters):
  """Play a game, selecting random actions."""

  def print_state(state):
    global message
    """Print some basic information about the state."""
    print("")
    print("Current player: {}".format(state.cur_player()))
    message = message+"Current player: {}".format(state.cur_player())+"\r\n"
    print(state)
    # Example of more queries to provide more about this state. For
    # example, bots could use these methods to to get information
    # about the state in order to act accordingly.
    print("### Information about the state retrieved separately ###")
    print("### Information tokens: {}".format(state.information_tokens()))
    print("### Life tokens: {}".format(state.life_tokens()))
    print("### Fireworks: {}".format(state.fireworks()))
    print("### Deck size: {}".format(state.deck_size()))
    print("### Discard pile: {}".format(str(state.discard_pile())))
    print("### Player hands: {}".format(str(state.player_hands())))
    # conn.send(bytes("Player hands: {}".format(str(state.player_hands()))+"\r\n",'UTF-8'))
    message = message+"Player hands: {}".format(str(state.player_hands()))+"\r\n"
    # data = conn.recv(1024)
    print("")

  def print_observation(observation):
    """Print some basic information about an agent observation."""
    print("--- Observation ---")
    print(observation)

    print("### Information about the observation retrieved separately ###")
    print("### Current player, relative to self: {}".format(
        observation.cur_player_offset()))
    print("### Observed hands: {}".format(observation.observed_hands()))
    print("### Card knowledge: {}".format(observation.card_knowledge()))
    print("### Discard pile: {}".format(observation.discard_pile()))
    print("### Fireworks: {}".format(observation.fireworks()))
    print("### Deck size: {}".format(observation.deck_size()))
    move_string = "### Last moves:"
    for move_tuple in observation.last_moves():
      move_string += " {}".format(move_tuple)
    print(move_string)
    print("### Information tokens: {}".format(observation.information_tokens()))
    print("### Life tokens: {}".format(observation.life_tokens()))
    print("### Legal moves: {}".format(observation.legal_moves()))
    print("--- EndObservation ---")
    ###
    # conn.send(bytes("### Observed hands: {}".format(observation.observed_hands())+"\r\n",'UTF-8'))
    # conn.send(bytes("### Fireworks: {}".format(observation.fireworks())+"\r\n",'UTF-8'))
    # data = conn.recv(1024)
    ###

  def print_encoded_observations(encoder, state, num_players):
    print("--- EncodedObservations ---")
    print("Observation encoding shape: {}".format(encoder.shape()))
    print("Current actual player: {}".format(state.cur_player()))
    for i in range(num_players):
      print("Encoded observation for player {}: {}".format(
          i, encoder.encode(state.observation(i)))) #HERE
      # print("W/O encode PLAYER {}: {}".format(i, state.observation(i)))
      # print("{} player end.".format(i))

    print("--- EndEncodedObservations ---")

  game = pyhanabi.HanabiGame(game_parameters)
  print(game.parameter_string(), end="")
  global message
  message  = message+ game.parameter_string()+"\r\n"
  obs_encoder = pyhanabi.ObservationEncoder(
      game, enc_type=pyhanabi.ObservationEncoderType.CANONICAL)

  state = game.new_initial_state()
  while not state.is_terminal():   #AND HERE
    if state.cur_player() == pyhanabi.CHANCE_PLAYER_ID:
      state.deal_random_card()
      continue

    print_state(state)

    observation = state.observation(state.cur_player())
    print_observation(observation)
    print_encoded_observations(obs_encoder, state, game.num_players())
    conn.send(bytes(message,'UTF-8'))
    data = conn.recv(1024)
    print(data.decode(encoding='UTF-8'))

    legal_moves = state.legal_moves()
    print("")
    print("Number of legal moves: {}".format(len(legal_moves)))

    move = np.random.choice(legal_moves)
    print("Chose random legal move: {}".format(move))

    state.apply_move(move)

  print("")
  print("Game done. Terminal state:")
  print("")
  print(state)
  print("")
  print("score: {}".format(state.score()))


if __name__ == "__main__":
  # Check that the cdef and library were loaded from the standard paths.
  assert pyhanabi.cdef_loaded(), "cdef failed to load"
  assert pyhanabi.lib_loaded(), "lib failed to load"
  run_game({"players": 2, "random_start_player": True}) #Youjin:was 3
  #TO DO: Encapsulating Java play agents

  # while(True):
      # conn.send(bytes("Message from Python"+"\r\n",'UTF-8'))
      # print("Message sent")
      # data = conn.recv(1024)
      # print(data.decode(encoding='UTF-8'))
