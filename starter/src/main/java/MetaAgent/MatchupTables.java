package MetaAgent;

public class MatchupTables {
	private static int [][][][] matchups = 
		{
				
//
//			{
//				{{8,13},{18,12},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
//				{{16,10},{13,10},{6,10},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
//				{{0,0},{16,10},{10,12},{11,13},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
//				{{18,10},{18,10},{6,14},{6,14},{6,10},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
//				{{18,10},{18,10},{12,12},{6,15},{6,12},{6,12},{6,12},{9,17},{8,13},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} }, 
//				{{19,19},{19,19},{16,10},{16,10},{16,10},{6,12},{6,12},{7,17},{6,12},{6,12},{6,10},{19,17},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
//				{{19,19},{18,10},{18,10},{18,10},{16,10},{16,10},{16,10},{12,12},{6,12},{6,12},{8,16},{9,15},{11,14},{11,14},{12,16},{14,18},{0,0},{0,0},{0,0},{0,0} },
//				{{7,1},{18,10},{19,6},{19,6},{18,5},{18,10},{16,10},{16,10},{14,12},{12,12},{14,12},{12,12},{14,14},{9,15},{9,16},{10,15},{14,18},{14,16},{14,16},{14,15} },
//				{{18,5},{18,11},{19,4},{18,5},{18,5},{19,5},{19,5},{18,5},{14,12},{14,12},{11,16},{12,12},{12,12},{10,14},{10,14},{10,14},{12,14},{9,16},{9,15},{9,15} },
//				{{19,4},{18,5},{18,5},{8,7},{8,7},{9,7},{8,7},{18,5},{18,5},{8,16},{12,12},{12,13},{12,12},{12,13},{10,14},{10,14},{10,14},{9,15},{9,16},{9,16} },
//				{{18,5},{18,5},{18,5},{8,7},{8,7},{9,8},{11,8},{11,8},{11,8},{8,16},{12,13},{12,12},{12,12},{12,13},{10,14},{10,14},{10,14},{10,14},{9,15},{9,15} },
//				{{11,12},{18,7},{18,10},{18,10},{8,7},{8,7},{8,7},{11,8},{11,8},{9,17},{12,13},{12,13},{9,15},{12,12},{10,14},{10,14},{9,16},{9,15},{10,14},{10,15} },
//				{{15,11},{14,7},{15,11},{15,11},{10,14},{8,7},{8,7},{11,19},{9,17},{9,17},{8,15},{9,16},{9,14},{8,14},{10,14},{9,16},{8,15},{11,17},{10,14},{10,14} },
//				{{18,11},{14,7},{15,11},{10,14},{10,14},{10,15},{11,17},{8,15},{9,15},{9,17},{9,14},{8,15},{8,14},{9,14},{9,14},{10,15},{10,15},{10,15},{10,15},{9,16} },
//				{{15,11},{10,12},{12,11},{10,14},{11,16},{11,16},{11,17},{9,14},{9,14},{9,17},{8,16},{9,14},{9,14},{8,14},{8,14},{10,15},{9,16},{10,15},{10,15},{9,16} },
//				{{10,12},{10,12},{10,14},{10,14},{11,19},{11,19},{11,17},{9,17},{10,14},{9,17},{8,16},{8,14},{12,13},{8,16},{9,15},{8,15},{9,15},{10,17},{9,16},{9,15} },
//				{{18,8},{12,11},{11,16},{10,14},{8,16},{8,16},{12,17},{9,16},{8,16},{8,16},{8,16},{8,15},{8,16},{8,16},{9,15},{9,15},{11,17},{12,17},{10,14},{9,16} },
//				{{12,11},{11,12},{10,14},{10,14},{8,16},{11,19},{12,17},{8,15},{8,14},{9,17},{9,16},{8,16},{8,16},{8,16},{10,14},{10,14},{10,14},{10,17},{10,14},{10,17} },
//				{{10,12},{10,12},{10,12},{10,14},{8,16},{11,16},{9,17},{8,15},{9,14},{8,16},{8,16},{8,16},{8,15},{10,14},{10,14},{10,14},{11,17},{10,17},{10,17},{10,14} },
//				{{0,1},{7,17},{7,17},{10,12},{7,19},{11,16},{11,16},{8,15},{8,15},{11,16},{8,16},{8,16},{8,16},{10,14},{10,14},{10,14},{8,16},{10,17},{10,17},{10,17} },
//			},
//				{ // 2P run 2 of ToG experiments
//				{{16,8},{17,8},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
//				{{8,11},{9,12},{18,13},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
//				{{17,11},{9,12},{18,12},{7,10},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
//				{{17,11},{17,11},{10,10},{10,10},{6,13},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
//				{{14,17},{14,17},{16,5},{17,11},{6,13},{6,13},{6,13},{7,10},{7,10},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
//				{{17,11},{17,11},{17,11},{15,9},{15,11},{6,13},{6,13},{6,13},{6,12},{6,12},{6,13},{17,11},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
//				{{6,14},{19,9},{19,9},{19,9},{15,11},{15,11},{13,11},{13,11},{15,9},{6,12},{11,14},{11,14},{13,16},{13,18},{13,18},{18,15},{0,0},{0,0},{0,0},{0,0} },
//				{{6,13},{19,9},{19,9},{19,9},{19,9},{19,9},{10,13},{15,9},{14,10},{9,12},{11,14},{11,14},{12,18},{14,17},{13,14},{8,14},{13,18},{12,17},{12,17},{12,17} },
//				{{19,9},{19,9},{19,9},{17,8},{19,9},{19,9},{19,9},{19,9},{9,18},{9,18},{11,14},{9,13},{9,13},{10,14},{8,14},{11,14},{9,15},{11,15},{11,15},{11,15} },
//				{{19,8},{19,9},{9,5},{19,9},{19,9},{19,9},{19,9},{19,9},{9,18},{9,18},{9,18},{9,12},{9,12},{10,18},{11,14},{11,14},{11,14},{11,15},{11,15},{8,15} },
//				{{19,8},{19,8},{19,9},{19,9},{19,9},{10,8},{10,8},{19,9},{19,9},{9,18},{10,18},{10,12},{9,15},{10,18},{9,15},{9,15},{9,15},{8,16},{8,16},{8,16} },
//				{{19,8},{19,8},{9,15},{8,15},{8,15},{19,9},{19,9},{19,9},{19,9},{9,18},{9,18},{9,18},{10,18},{10,18},{9,15},{9,15},{9,15},{8,16},{8,15},{8,16} },
//				{{9,13},{12,11},{9,12},{8,15},{8,15},{7,15},{11,8},{19,9},{19,9},{9,18},{9,18},{8,16},{10,18},{8,15},{9,15},{9,16},{10,17},{8,16},{8,16},{9,15} },
//				{{9,13},{15,11},{12,11},{9,18},{8,17},{6,13},{9,12},{9,18},{9,18},{9,18},{9,18},{9,18},{9,18},{9,15},{10,14},{10,14},{10,15},{8,16},{8,16},{8,16} },
//				{{10,13},{9,12},{12,11},{9,15},{7,16},{6,13},{9,12},{9,18},{9,18},{9,18},{9,18},{9,18},{9,18},{9,15},{10,14},{10,14},{10,15},{8,15},{8,16},{9,15} },
//				{{12,11},{12,11},{9,15},{9,15},{7,16},{11,12},{7,17},{9,18},{9,18},{9,18},{9,18},{9,18},{9,18},{9,12},{9,14},{9,14},{9,15},{8,16},{8,16},{8,16} },
//				{{14,7},{12,11},{9,15},{8,15},{6,13},{9,18},{7,17},{9,18},{9,18},{9,18},{9,18},{9,18},{9,13},{9,18},{10,15},{9,15},{9,15},{9,15},{9,15},{9,18} },
//				{{11,12},{14,10},{9,15},{9,15},{6,13},{9,18},{7,17},{9,18},{9,18},{9,18},{9,18},{9,18},{9,12},{10,12},{10,15},{9,15},{9,15},{8,15},{9,18},{9,18} },
//				{{12,11},{12,11},{12,11},{6,13},{6,13},{9,18},{7,17},{9,18},{9,18},{9,18},{9,18},{9,18},{9,18},{9,12},{8,15},{9,18},{9,18},{9,15},{9,15},{9,15} },
//				{{6,13},{6,14},{6,14},{6,13},{6,13},{7,18},{7,18},{7,17},{9,18},{9,18},{9,18},{9,18},{9,15},{9,15},{9,15},{9,18},{9,18},{9,18},{9,18},{9,16}}
//				},
//			
			// 2P run 3 of ToG experiments
			{
				{{0,0},{12,11},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
				{{18,9},{13,13},{11,10},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
				{{18,12},{13,12},{18,9},{18,9},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
				{{18,12},{18,6},{18,9},{18,9},{19,10},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
				{{18,9},{18,6},{18,6},{18,9},{18,9},{7,10},{13,19},{13,19},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
				{{18,8},{18,8},{18,8},{10,12},{10,12},{7,11},{7,11},{6,12},{13,15},{13,15},{13,18},{19,16},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0} },
				{{18,8},{18,9},{18,9},{18,9},{10,12},{11,10},{9,13},{11,12},{13,15},{6,12},{8,14},{8,14},{13,14},{13,14},{11,18},{10,17},{0,0},{0,0},{0,0},{0,0} },
				{{19,1},{18,9},{18,9},{19,9},{18,9},{18,9},{10,12},{10,12},{11,13},{6,12},{10,14},{10,14},{13,15},{13,15},{10,17},{10,17},{10,17},{10,17},{10,17},{9,18} },
				{{18,9},{18,9},{8,2},{19,9},{18,9},{18,9},{18,9},{18,9},{11,13},{11,12},{11,13},{9,12},{10,13},{10,17},{10,14},{11,14},{11,16},{10,16},{10,14},{9,17} },
				{{18,9},{18,9},{18,9},{19,9},{18,9},{18,9},{19,9},{19,9},{10,13},{10,13},{10,13},{10,13},{10,13},{9,17},{10,15},{10,15},{11,16},{9,17},{11,17},{11,14} },
				{{18,9},{18,9},{18,9},{18,9},{18,9},{19,9},{19,9},{18,9},{18,9},{10,13},{9,13},{10,13},{10,13},{10,13},{10,14},{10,14},{10,14},{11,16},{10,15},{11,15} },
				{{9,15},{18,9},{18,9},{10,15},{11,15},{18,9},{18,9},{18,9},{18,9},{10,17},{10,18},{9,16},{10,13},{10,13},{10,14},{11,15},{11,14},{11,17},{10,16},{10,14} },
				{{8,15},{9,17},{12,12},{10,14},{10,15},{11,17},{18,9},{18,9},{18,9},{10,17},{9,18},{9,17},{12,14},{12,14},{11,16},{11,16},{11,16},{11,16},{11,16},{10,15} },
				{{9,12},{9,12},{9,12},{11,14},{10,17},{11,17},{13,17},{10,13},{10,17},{10,17},{10,13},{9,17},{12,14},{12,14},{11,16},{10,16},{10,16},{10,16},{10,16},{9,18} },
				{{9,17},{9,12},{9,12},{11,16},{10,17},{13,17},{9,17},{10,13},{10,17},{10,17},{8,16},{8,16},{8,16},{12,14},{10,16},{10,16},{10,16},{10,16},{9,18},{10,16} },
				{{9,13},{8,15},{15,11},{10,17},{13,17},{13,17},{8,16},{10,13},{10,17},{8,16},{9,16},{9,16},{8,16},{10,17},{11,15},{10,17},{11,17},{11,17},{9,18},{9,18} },
				{{10,12},{8,15},{14,12},{11,16},{13,17},{8,16},{8,16},{10,13},{10,17},{9,16},{9,16},{10,13},{10,13},{8,16},{8,16},{10,17},{10,17},{10,17},{10,17},{10,16} },
				{{9,12},{11,17},{7,13},{7,13},{8,16},{9,17},{8,16},{10,13},{10,13},{10,13},{10,13},{10,13},{10,13},{10,17},{10,17},{9,17},{11,17},{10,17},{10,17},{11,16} },
				{{10,9},{10,12},{10,12},{7,13},{8,18},{9,16},{8,18},{10,13},{10,17},{9,16},{8,16},{9,16},{9,16},{8,16},{10,17},{9,17},{10,17},{10,17},{10,17},{10,17} },
				{{7,0},{7,13},{7,13},{7,13},{9,17},{8,18},{8,16},{9,16},{10,17},{9,16},{8,16},{8,16},{9,16},{9,16},{10,17},{10,17},{10,17},{10,17},{10,17},{9,17}},
			},
//				
//			{ // 2P with new metrics, 
//				TODO: but does not correspond to any experiment I could find
//				{{13,6},{18,12},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
//				{{12,11},{12,10},{13,6},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
//				{{0,0},{12,11},{14,12},{13,14},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
//				{{18,10},{19,12},{6,14},{6,15},{12,8},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
//				{{19,12},{18,10},{12,8},{6,15},{6,12},{6,12},{6,12},{10,15},{10,15},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
//				{{19,19},{19,19},{16,10},{16,10},{16,10},{6,12},{6,12},{7,17},{6,12},{6,12},{6,10},{15,18},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
//				{{19,19},{18,10},{18,10},{18,10},{16,10},{16,10},{10,11},{16,10},{6,12},{6,12},{11,19},{11,19},{11,17},{11,17},{11,16},{13,18},{0,0},{0,0},{0,0},{0,0}},
//				{{7,1},{18,10},{18,10},{18,5},{18,10},{18,10},{10,11},{10,11},{10,11},{14,12},{13,12},{12,12},{8,14},{13,14},{13,14},{10,15},{10,16},{10,17},{10,17},{9,16}},
//				{{18,10},{18,10},{18,10},{18,5},{18,5},{19,6},{18,5},{18,5},{10,11},{10,11},{8,16},{12,12},{12,12},{10,14},{9,15},{9,15},{9,16},{9,16},{9,16},{9,16}},
//				{{18,10},{19,5},{18,5},{8,7},{8,7},{8,7},{8,7},{18,5},{18,5},{11,19},{12,12},{12,12},{12,12},{12,13},{9,15},{10,14},{10,14},{9,15},{9,16},{9,16}},
//				{{18,5},{18,5},{18,5},{8,7},{8,7},{9,8},{11,8},{11,8},{11,8},{8,16},{12,13},{12,12},{8,16},{12,12},{10,14},{10,14},{10,14},{10,14},{9,15},{9,15}},
//				{{10,13},{18,7},{14,7},{18,10},{8,7},{8,7},{8,7},{11,8},{11,8},{9,17},{12,13},{12,13},{8,16},{8,16},{10,14},{10,14},{9,16},{10,14},{9,16},{10,15}},
//				{{12,11},{14,7},{15,11},{15,11},{11,16},{9,8},{9,7},{11,19},{9,12},{8,16},{8,16},{8,14},{9,14},{8,14},{10,14},{10,15},{9,16},{10,17},{10,14},{9,16}},
//				{{15,12},{14,7},{15,11},{11,16},{11,16},{10,15},{11,19},{8,16},{8,16},{9,17},{8,14},{8,15},{8,15},{8,16},{8,14},{10,15},{10,15},{9,15},{9,15},{10,15}},
//				{{15,11},{11,13},{12,11},{11,16},{11,16},{11,16},{11,19},{8,16},{8,16},{8,16},{8,16},{9,14},{8,15},{8,14},{8,14},{10,15},{8,16},{9,16},{9,15},{10,15}},
//				{{11,13},{11,12},{10,14},{11,16},{11,19},{11,19},{11,19},{8,16},{8,16},{9,17},{8,16},{9,14},{12,12},{12,13},{10,14},{8,14},{8,16},{8,16},{9,15},{9,16}},
//				{{18,8},{11,13},{10,14},{11,16},{8,16},{8,17},{12,17},{8,16},{8,16},{8,16},{8,16},{8,16},{10,13},{8,16},{10,14},{10,14},{10,14},{10,17},{10,14},{9,15}},
//				{{18,8},{11,13},{10,14},{10,14},{7,18},{11,19},{12,17},{8,16},{8,16},{8,16},{8,15},{8,16},{8,16},{8,16},{10,14},{10,14},{10,14},{10,17},{10,17},{10,14}},
//				{{10,12},{11,12},{10,12},{10,14},{8,16},{8,18},{9,17},{8,16},{9,12},{8,16},{8,16},{8,16},{9,12},{8,14},{8,14},{10,14},{12,17},{10,17},{11,17},{10,14}},
//				{{0,1},{7,18},{7,17},{11,13},{8,18},{11,16},{8,18},{11,16},{9,13},{11,16},{8,16},{8,16},{8,16},{9,14},{8,14},{8,16},{8,16},{10,17},{10,17},{10,17}}
//				},
//			{ //2p with old metrics
//				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {17,16}, {5,11}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
//				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {7,13}, {9,18}, {13,13}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
//				{ {0,0}, {0,0}, {0,0}, {0,0}, {14,16}, {19,15}, {14,16}, {14,16}, {14,18}, {7,13}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
//				{ {0,0}, {0,0}, {0,0}, {0,0}, {9,18}, {7,14}, {7,13}, {8,18}, {7,13}, {7,13}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
//				{ {0,0}, {0,0}, {0,0}, {8,17}, {6,16}, {6,16}, {6,16}, {6,17}, {7,13}, {6,16}, {6,16}, {19,12}, {0,0}, {8,14}, {9,16}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
//				{ {0,0}, {0,0}, {8,14}, {8,17}, {12,19}, {6,16}, {9,19}, {6,16}, {6,16}, {6,17}, {6,16}, {6,16}, {6,16}, {14,17}, {15,19}, {15,19}, {19,12}, {19,12}, {16,17}, {0,0} }, 
//				{ {14,14}, {14,16}, {12,19}, {13,18}, {6,16}, {6,16}, {6,16}, {6,16}, {6,16}, {6,17}, {6,16}, {6,16}, {11,19}, {9,19}, {14,19}, {9,19}, {9,18}, {9,18}, {16,19}, {13,18} }, 
//				{ {9,18}, {14,16}, {14,16}, {12,19}, {13,18}, {6,16}, {6,16}, {9,19}, {8,16}, {10,17}, {7,18}, {9,18}, {9,18}, {9,18}, {9,18}, {9,18}, {9,19}, {9,19}, {12,17}, {13,18} }, 
//				{ {17,18}, {16,15}, {14,16}, {12,19}, {10,12}, {10,12}, {8,16}, {8,16}, {8,16}, {7,19}, {8,16}, {10,17}, {10,18}, {10,18}, {10,17}, {10,17}, {10,17}, {9,18}, {9,16}, {9,16} }, 
//				{ {17,18}, {8,10}, {8,17}, {7,18}, {6,17}, {10,12}, {9,11}, {9,19}, {9,19}, {10,17}, {10,17}, {10,17}, {9,16}, {10,17}, {10,18}, {10,17}, {10,17}, {9,18}, {9,16}, {9,16} }, 
//				{ {6,16}, {14,16}, {18,18}, {7,18}, {6,16}, {7,18}, {9,11}, {9,18}, {7,18}, {10,17}, {10,17}, {10,17}, {9,16}, {10,17}, {10,18}, {10,17}, {10,17}, {10,16}, {9,16}, {9,16} }, 
//				{ {6,16}, {14,13}, {14,16}, {9,18}, {6,16}, {9,18}, {8,16}, {8,16}, {9,18}, {9,18}, {11,18}, {9,19}, {9,16}, {10,17}, {10,18}, {13,17}, {10,16}, {10,16}, {9,16}, {9,16} }, 
//				{ {15,16}, {7,18}, {7,18}, {7,16}, {6,17}, {9,18}, {7,16}, {8,16}, {9,18}, {9,19}, {10,17}, {9,19}, {9,18}, {9,18}, {10,17}, {11,17}, {10,16}, {9,16}, {9,16}, {9,16} }, 
//				{ {9,18}, {7,19}, {7,19}, {7,19}, {9,18}, {9,19}, {8,16}, {7,18}, {7,18}, {10,17}, {9,19}, {9,19}, {9,18}, {9,18}, {10,18}, {11,17}, {9,16}, {10,16}, {10,16}, {9,16} }, 
//				{ {0,0}, {2,8}, {7,19}, {7,18}, {9,18}, {7,18}, {8,16}, {8,16}, {9,18}, {10,17}, {10,17}, {10,17}, {9,16}, {9,18}, {9,18}, {9,16}, {9,18}, {10,18}, {9,16}, {9,16} }, 
//				{ {0,0}, {7,18}, {7,18}, {7,18}, {7,19}, {7,19}, {7,19}, {7,19}, {10,17}, {9,19}, {10,17}, {10,17}, {9,19}, {10,17}, {10,16}, {9,16}, {9,19}, {9,16}, {9,16}, {10,18} }, 
//				{ {0,0}, {7,18}, {7,19}, {7,18}, {7,19}, {8,16}, {8,16}, {8,16}, {9,19}, {9,19}, {10,17}, {10,17}, {8,16}, {8,16}, {10,18}, {8,19}, {10,17}, {11,19}, {11,19}, {10,17} }, 
//				{ {2,8}, {6,18}, {7,19}, {7,18}, {7,18}, {8,16}, {8,16}, {7,18}, {12,19}, {9,19}, {9,18}, {9,18}, {16,15}, {18,15}, {10,17}, {10,17}, {10,19}, {10,19}, {10,17}, {12,16} }, 
//				{ {6,18}, {6,18}, {6,18}, {7,18}, {6,18}, {6,18}, {6,18}, {7,19}, {10,17}, {10,17}, {9,16}, {10,18}, {11,18}, {10,18}, {12,18}, {10,18}, {10,19}, {10,17}, {10,17}, {10,17} }, 
//				{ {0,0}, {7,18}, {7,18}, {6,18}, {7,18}, {8,16}, {7,19}, {10,17}, {10,17}, {10,17}, {12,18}, {10,17}, {12,19}, {8,18}, {8,18}, {10,17}, {10,19}, {10,19}, {10,17}, {10,17} }, 
//				},
			{ //3P
				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {2,8}, {14,18}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {6,14}, {16,17}, {17,16}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {13,15}, {11,17}, {17,16}, {14,18}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {14,18}, {15,17}, {11,17}, {19,17}, {14,18}, {14,18}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {14,18}, {13,19}, {13,19}, {13,19}, {13,19}, {13,19}, {13,19}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {16,17}, {16,17}, {15,18}, {14,18}, {15,18}, {15,17}, {12,17}, {13,17}, {12,17}, {16,17}, {16,17}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {13,19}, {13,19}, {13,19}, {8,17}, {14,18}, {15,18}, {15,17}, {12,17}, {16,17}, {13,16}, {12,16}, {11,17}, {11,17}, {11,18}, {13,18}, {13,18}, {18,18}, {0,0} }, 
				{ {15,17}, {15,17}, {15,18}, {14,19}, {14,19}, {14,19}, {6,10}, {6,16}, {6,16}, {15,18}, {15,17}, {14,18}, {13,19}, {14,17}, {13,18}, {12,17}, {13,17}, {13,18}, {13,18}, {14,18} }, 
				{ {13,19}, {14,18}, {14,18}, {14,18}, {14,19}, {9,13}, {7,17}, {7,19}, {6,16}, {15,18}, {14,18}, {13,19}, {13,18}, {14,17}, {14,18}, {12,17}, {13,17}, {13,18}, {13,18}, {13,19} }, 
				{ {14,18}, {8,17}, {11,19}, {11,19}, {10,18}, {7,19}, {11,18}, {11,18}, {6,16}, {11,18}, {15,18}, {11,17}, {13,18}, {11,17}, {14,18}, {13,17}, {14,17}, {15,17}, {14,18}, {13,18} }, 
				{ {18,17}, {11,19}, {11,19}, {19,19}, {6,17}, {11,17}, {11,18}, {10,18}, {11,17}, {14,18}, {13,18}, {14,18}, {14,18}, {13,18}, {14,18}, {13,17}, {13,17}, {12,17}, {13,18}, {13,18} }, 
				{ {18,18}, {16,18}, {6,17}, {13,19}, {6,17}, {11,19}, {11,17}, {11,17}, {12,18}, {13,19}, {13,18}, {13,19}, {11,18}, {14,17}, {13,17}, {11,17}, {12,17}, {12,17}, {13,17}, {12,17} }, 
				{ {18,17}, {19,18}, {16,18}, {7,19}, {10,19}, {7,18}, {7,19}, {10,18}, {11,17}, {8,17}, {13,19}, {11,17}, {13,18}, {11,17}, {11,17}, {13,19}, {12,17}, {13,17}, {11,17}, {12,17} }, 
				{ {9,19}, {16,17}, {9,19}, {10,18}, {10,19}, {10,18}, {10,18}, {11,18}, {8,17}, {11,19}, {11,17}, {13,18}, {11,17}, {13,18}, {11,17}, {11,17}, {13,17}, {13,17}, {11,17}, {12,17} }, 
				{ {19,17}, {9,19}, {10,18}, {10,18}, {8,17}, {9,19}, {10,18}, {11,17}, {14,18}, {11,17}, {13,18}, {14,18}, {11,17}, {11,17}, {11,18}, {12,17}, {13,17}, {11,17}, {11,17}, {13,18} }, 
				{ {9,15}, {9,15}, {10,18}, {10,18}, {10,18}, {10,18}, {10,18}, {11,18}, {10,19}, {14,18}, {11,17}, {11,17}, {11,18}, {11,17}, {13,19}, {13,19}, {13,18}, {11,17}, {11,17}, {11,18} }, 
				{ {0,0}, {9,15}, {10,18}, {10,18}, {10,18}, {11,17}, {11,18}, {12,18}, {10,19}, {11,19}, {14,18}, {13,18}, {11,18}, {11,17}, {11,18}, {13,19}, {13,18}, {12,17}, {13,18}, {13,18} }, 
				{ {18,18}, {16,18}, {10,18}, {10,18}, {10,18}, {11,17}, {12,18}, {10,18}, {10,19}, {11,17}, {14,18}, {14,18}, {11,17}, {11,17}, {11,18}, {13,18}, {11,18}, {13,19}, {11,18}, {13,18} }, 
				{ {14,19}, {18,17}, {9,19}, {9,19}, {11,18}, {10,18}, {9,19}, {11,19}, {9,19}, {14,18}, {11,18}, {14,18}, {11,18}, {11,18}, {13,18}, {11,17}, {11,18}, {11,17}, {11,17}, {11,19} }, 
				{ {0,0}, {10,19}, {10,18}, {10,18}, {11,17}, {12,18}, {11,17}, {11,18}, {14,19}, {11,19}, {16,18}, {12,18}, {11,18}, {11,19}, {12,19}, {10,18}, {13,19}, {11,17}, {11,17}, {11,19} }, 
				},
			{ //4p
				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {18,15}, {10,14}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {12,19}, {13,16}, {13,16}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {13,16}, {12,15}, {13,16}, {11,18}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {13,18}, {14,15}, {13,19}, {13,16}, {13,18}, {12,15}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {13,18}, {14,15}, {14,17}, {11,17}, {11,17}, {13,18}, {13,18}, {13,18}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {14,17}, {13,18}, {12,17}, {13,18}, {13,18}, {15,17}, {15,17}, {13,18}, {13,18}, {15,17}, {13,13}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {14,18}, {14,17}, {13,19}, {12,17}, {14,13}, {13,13}, {13,18}, {13,18}, {13,18}, {15,17}, {13,16}, {12,17}, {12,17}, {14,16}, {0,0}, {0,0} }, 
				{ {15,18}, {13,19}, {0,0}, {14,17}, {14,17}, {13,17}, {13,13}, {7,19}, {7,18}, {7,18}, {13,18}, {12,17}, {13,17}, {14,16}, {6,14}, {13,17}, {12,17}, {12,15}, {13,15}, {12,16} }, 
				{ {14,17}, {13,19}, {13,19}, {14,17}, {13,18}, {11,18}, {7,19}, {7,19}, {7,18}, {7,18}, {15,17}, {13,16}, {15,17}, {13,18}, {12,16}, {13,16}, {12,15}, {12,16}, {12,15}, {12,15} }, 
				{ {14,18}, {15,18}, {12,17}, {12,18}, {12,18}, {12,18}, {12,18}, {12,15}, {13,15}, {12,18}, {12,18}, {14,18}, {14,16}, {14,16}, {14,16}, {12,15}, {12,15}, {12,15}, {12,16}, {12,15} }, 
				{ {0,0}, {9,17}, {9,17}, {12,18}, {8,17}, {12,18}, {12,18}, {12,15}, {11,17}, {15,17}, {12,18}, {13,15}, {14,16}, {13,18}, {12,15}, {12,15}, {12,15}, {14,15}, {12,15}, {12,17} }, 
				{ {16,15}, {0,0}, {9,17}, {9,17}, {7,17}, {7,19}, {14,17}, {9,19}, {9,18}, {12,18}, {14,18}, {12,17}, {12,18}, {13,18}, {12,15}, {13,16}, {13,16}, {12,15}, {14,15}, {12,17} }, 
				{ {16,15}, {9,17}, {8,17}, {7,17}, {7,18}, {12,18}, {9,17}, {12,19}, {9,19}, {12,19}, {13,18}, {13,18}, {14,18}, {14,16}, {12,14}, {12,15}, {12,15}, {12,17}, {14,15}, {13,15} }, 
				{ {11,15}, {13,19}, {9,18}, {7,18}, {8,17}, {9,17}, {9,17}, {9,19}, {11,16}, {10,13}, {13,18}, {14,17}, {13,19}, {13,18}, {12,14}, {12,15}, {12,15}, {12,17}, {12,15}, {13,15} }, 
				{ {11,15}, {14,16}, {9,17}, {9,17}, {8,17}, {8,17}, {11,18}, {9,19}, {9,19}, {13,18}, {14,19}, {12,17}, {14,17}, {14,17}, {13,16}, {13,16}, {13,16}, {12,15}, {12,15}, {12,16} }, 
				{ {18,16}, {11,15}, {9,17}, {8,17}, {9,17}, {8,17}, {8,17}, {8,17}, {9,17}, {13,18}, {13,18}, {14,18}, {15,17}, {14,18}, {13,16}, {13,16}, {13,16}, {12,17}, {15,17}, {15,17} }, 
				{ {18,16}, {11,15}, {8,17}, {8,17}, {9,17}, {8,17}, {9,17}, {8,17}, {14,18}, {13,18}, {12,18}, {13,18}, {15,17}, {14,18}, {9,17}, {13,16}, {12,15}, {12,15}, {15,17}, {12,17} }, 
				{ {11,15}, {11,15}, {9,17}, {8,18}, {9,18}, {8,17}, {8,17}, {13,19}, {14,18}, {14,18}, {13,18}, {14,19}, {13,19}, {12,17}, {13,15}, {12,15}, {12,15}, {12,15}, {12,17}, {12,15} }, 
				{ {9,19}, {11,15}, {9,17}, {9,17}, {9,17}, {8,17}, {8,17}, {9,17}, {9,17}, {15,17}, {13,19}, {14,17}, {15,17}, {12,18}, {13,15}, {12,15}, {12,15}, {12,15}, {13,17}, {14,16} }, 
				{ {9,17}, {8,17}, {8,17}, {8,18}, {14,17}, {15,19}, {14,19}, {8,17}, {8,17}, {13,18}, {14,17}, {14,17}, {14,18}, {14,18}, {15,18}, {12,15}, {12,15}, {15,17}, {15,17}, {14,16} }
				},
			{ //5p
				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {8,15}, {13,14}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {13,14}, {14,16}, {14,17}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {13,14}, {14,17}, {14,17}, {13,16}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {15,16}, {13,16}, {14,17}, {14,15}, {13,15}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {13,14}, {9,17}, {15,16}, {13,14}, {13,14}, {13,15}, {13,15}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {14,17}, {14,17}, {14,17}, {14,17}, {14,17}, {14,17}, {13,15}, {14,15}, {14,15}, {13,16}, {14,17}, {0,0}, {0,0}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {0,0}, {0,0}, {0,0}, {12,19}, {13,19}, {14,14}, {9,17}, {14,17}, {14,18}, {14,17}, {14,17}, {12,17}, {14,17}, {11,14}, {13,15}, {13,16}, {0,0}, {0,0}, {0,0} }, 
				{ {0,0}, {14,17}, {14,17}, {14,17}, {14,18}, {14,19}, {15,16}, {14,14}, {16,19}, {13,17}, {14,17}, {14,17}, {13,15}, {14,17}, {13,15}, {13,15}, {13,15}, {13,15}, {13,15}, {13,15} }, 
				{ {14,17}, {14,17}, {14,18}, {14,18}, {14,18}, {13,16}, {14,14}, {14,14}, {14,14}, {14,14}, {14,15}, {13,16}, {11,15}, {13,15}, {14,17}, {13,14}, {13,15}, {13,15}, {13,15}, {13,15} }, 
				{ {14,14}, {14,14}, {14,14}, {13,15}, {14,14}, {14,14}, {14,14}, {14,14}, {14,14}, {9,17}, {15,17}, {14,16}, {13,16}, {13,15}, {13,15}, {14,17}, {14,17}, {13,15}, {13,16}, {14,17} }, 
				{ {13,15}, {13,15}, {13,15}, {13,15}, {14,14}, {14,14}, {14,14}, {14,14}, {16,19}, {10,17}, {13,17}, {14,15}, {13,15}, {13,16}, {13,15}, {14,17}, {13,15}, {13,15}, {13,16}, {14,17} }, 
				{ {9,18}, {13,15}, {13,15}, {13,15}, {12,17}, {9,17}, {10,17}, {14,14}, {12,18}, {11,15}, {15,16}, {14,17}, {13,15}, {14,16}, {13,15}, {13,15}, {13,15}, {13,15}, {13,15}, {13,15} }, 
				{ {17,17}, {14,14}, {14,14}, {10,17}, {10,17}, {9,16}, {14,14}, {9,17}, {10,18}, {18,16}, {13,16}, {13,16}, {14,17}, {14,17}, {13,15}, {13,15}, {13,15}, {13,15}, {13,15}, {13,15} }, 
				{ {9,15}, {11,15}, {13,15}, {9,16}, {9,17}, {14,14}, {9,16}, {10,19}, {9,18}, {10,17}, {14,17}, {14,17}, {14,17}, {14,17}, {13,15}, {13,16}, {13,15}, {13,15}, {13,16}, {13,15} }, 
				{ {9,15}, {8,13}, {9,17}, {9,18}, {10,17}, {10,17}, {10,17}, {9,18}, {9,18}, {9,17}, {14,18}, {14,17}, {14,17}, {14,17}, {14,17}, {13,15}, {13,15}, {13,15}, {13,15}, {13,15} }, 
				{ {18,16}, {17,17}, {9,15}, {9,15}, {9,16}, {9,15}, {9,15}, {9,18}, {10,18}, {12,17}, {14,18}, {14,18}, {14,18}, {14,18}, {13,15}, {13,15}, {13,15}, {13,15}, {13,16}, {13,15} }, 
				{ {15,16}, {9,17}, {9,17}, {10,17}, {9,15}, {10,17}, {14,15}, {9,15}, {15,16}, {12,17}, {14,18}, {15,16}, {14,17}, {14,17}, {13,15}, {13,15}, {13,15}, {13,15}, {13,15}, {13,15} }, 
				{ {9,17}, {9,17}, {9,17}, {12,17}, {9,18}, {14,18}, {10,17}, {9,17}, {9,18}, {12,17}, {14,18}, {15,16}, {15,17}, {14,17}, {14,17}, {13,15}, {13,15}, {13,15}, {13,15}, {13,15} }, 
				{ {0,0}, {0,0}, {0,0}, {12,17}, {12,17}, {10,18}, {15,19}, {9,17}, {14,18}, {10,18}, {14,18}, {14,17}, {15,16}, {15,16}, {14,15}, {13,15}, {13,15}, {13,15}, {13,15}, {13,15} }, 
				{ {0,0}, {0,0}, {9,18}, {14,18}, {14,18}, {14,18}, {14,19}, {14,19}, {9,17}, {14,18}, {15,16}, {14,17}, {14,16}, {14,17}, {13,16}, {14,17}, {13,15}, {13,15}, {13,15}, {13,15} },
				}
		};
	
	public static int[][][] getMatchups(int numPlayers){
		return matchups[numPlayers-2];
				
	}
	
	public static void main (String args[]) {
		int[][][] table = getMatchups(2);
		for (int i =0; i < table.length;i++) {
			for(int j= 0; j<table[0].length;j++) {
				System.out.print("{"+table[i][j][0]+","+table[i][j][1]+"} ");
			}
			System.out.println("");
		}
		
		
		
	}
}
