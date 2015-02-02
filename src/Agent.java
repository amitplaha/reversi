import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Agent {

	private int task;

	private char player;

	private char opponent;

	private int cutOffDepth;

	private float processingTimeLeft;

	private char initialState[][] = new char[8][8];

	private String positionNameMatrix[][] = new String[8][8];

	private Map<Integer, Character> numberAlphaMapping = new HashMap<Integer, Character>();

	private Map<Character, Integer> alphaNumberMapping = new HashMap<Character, Integer>();

	private int positionalWeights[][] = { { 99, -8, 8, 6, 6, 8, -8, 99 },
			{ -8, -24, -4, -3, -3, -4, -24, -8 }, { 8, -4, 7, 4, 4, 7, -4, 8 },
			{ 6, -3, 4, 0, 0, 4, -3, 6 }, { 6, -3, 4, 0, 0, 4, -3, 6 },
			{ 8, -4, 7, 4, 4, 7, -4, 8 }, { -8, -24, -4, -3, -3, -4, -24, -8 },
			{ 99, -8, 8, 6, 6, 8, -8, 99 } };

	/**
	 * @return the processingTimeLeft
	 */
	public float getProcessingTimeLeft() {
		return processingTimeLeft;
	}

	/**
	 * @return the task
	 */
	public int getTask() {
		return task;
	}

	/**
	 * @param task
	 *            the task to set
	 */
	public void setTask(int task) {
		this.task = task;
	}

	/**
	 * @return the player
	 */
	public char getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(char player) {
		this.player = player;
	}

	/**
	 * @return the cutOffDepth
	 */
	public int getCutOffDepth() {
		return cutOffDepth;
	}

	/**
	 * @param cutOffDepth
	 *            the cutOffDepth to set
	 */
	public void setCutOffDepth(int cutOffDepth) {
		this.cutOffDepth = cutOffDepth;
	}

	/**
	 * @return the initialState
	 */
	public char[][] getInitialState() {
		return initialState;
	}

	/**
	 * @param initialState
	 *            the initialState to set
	 */
	public void setInitialState(char[][] initialState) {
		this.initialState = initialState;
	}

	/**
	 * @return the positionNameMatrix
	 */
	public String[][] getPositionNameMatrix() {
		return positionNameMatrix;
	}

	/**
	 * @param positionNameMatrix
	 *            the positionNameMatrix to set
	 */
	public void setPositionNameMatrix(String[][] positionNameMatrix) {
		this.positionNameMatrix = positionNameMatrix;
	}

	/**
	 * @return the numberAlphaMapping
	 */
	public Map<Integer, Character> getNumberAlphaMapping() {
		return numberAlphaMapping;
	}

	/**
	 * @param numberAlphaMapping
	 *            the numberAlphaMapping to set
	 */
	public void setNumberAlphaMapping(Map<Integer, Character> numberAlphaMapping) {
		this.numberAlphaMapping = numberAlphaMapping;
	}

	/**
	 * @return the opponent
	 */
	public char getOpponent() {
		return opponent;
	}

	private void setOpponent(char player) {
		if (player == 'X') {
			opponent = 'O';
		} else if (player == 'O') {
			opponent = 'X';
		}
	}

	/**
	 * The method reads the input file and sets the initial state.
	 */
	private void readInputFile() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					"input.txt"));

			// obtain the task from the input file.
			task = Integer.parseInt(bufferedReader.readLine());

			// obtain our player.
			player = bufferedReader.readLine().charAt(0);

			setOpponent(player);

			// obtain the processing time left
			processingTimeLeft = Float.parseFloat(bufferedReader.readLine());

			// set the initial state of the game.
			for (int i = 0; i < 8; i++) {
				String row = bufferedReader.readLine();
				for (int j = 0; j < 8; j++) {
					initialState[i][j] = row.charAt(j);
				}
			}

			setPositionNameMatrix();

			setAlphaNumberMapping();

			bufferedReader.close();
		} catch (IOException ioException) {
			System.err.format("IO Exception: " + ioException);
		}
	}

	private void setNumberAlphaMapping() {
		numberAlphaMapping.put(0, 'a');
		numberAlphaMapping.put(1, 'b');
		numberAlphaMapping.put(2, 'c');
		numberAlphaMapping.put(3, 'd');
		numberAlphaMapping.put(4, 'e');
		numberAlphaMapping.put(5, 'f');
		numberAlphaMapping.put(6, 'g');
		numberAlphaMapping.put(7, 'h');
	}

	private void setAlphaNumberMapping() {
		alphaNumberMapping.put('a', 0);
		alphaNumberMapping.put('b', 1);
		alphaNumberMapping.put('c', 2);
		alphaNumberMapping.put('d', 3);
		alphaNumberMapping.put('e', 4);
		alphaNumberMapping.put('f', 5);
		alphaNumberMapping.put('g', 6);
		alphaNumberMapping.put('h', 7);

	}

	private void setPositionNameMatrix() {
		setNumberAlphaMapping();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				positionNameMatrix[i][j] = numberAlphaMapping.get(j)
						+ Integer.toString(i + 1);
			}
		}
	}

	private void addMove(List<String> moves, String move) {
		if (move != null && !moves.contains(move)) {
			moves.add(move);
		}
	}

	/**
	 * The method checks the left horizontal for valid moves.
	 * 
	 * @param state
	 * @param player
	 * @param row
	 * @param column
	 * @param opponent
	 * @return
	 */
	private String checkLeftHorizontal(char state[][], char player, int row,
			int column, char opponent) {

		boolean isValid = false;
		if (state[row][column - 1] == '*' || state[row][column - 1] == player) {
			// contains no valid moves.
			return null;
		} else {
			column--;
			while (column > 0) {
				column--;
				if (state[row][column] == player) {
					return null;
				} else if (state[row][column] == '*') {
					isValid = true;
					break;
				}
			}
		}
		if (isValid) {
			String move = numberAlphaMapping.get(column)
					+ Integer.toString(row + 1);
			return move;
		} else {
			return null;
		}
	}

	/**
	 * The method checks the right horizontal for valid moves.
	 * 
	 * @param state
	 * @param player
	 * @param row
	 * @param column
	 * @param opponent
	 * @return
	 */
	private String checkRightHorizontal(char state[][], char player, int row,
			int column, char opponent) {
		boolean isValid = false;
		if (state[row][column + 1] == '*' || state[row][column + 1] == player) {
			// contains no valid move
			return null;
		} else {
			column++;
			while (column < 7) {
				column++;
				if (state[row][column] == player) {
					return null;
				} else if (state[row][column] == '*') {
					isValid = true;
					break;
				}
			}
		}
		if (isValid) {
			String move = numberAlphaMapping.get(column)
					+ Integer.toString(row + 1);
			return move;
		} else {
			return null;
		}
	}

	/**
	 * The method checks upwards for valid moves.
	 * 
	 * @param state
	 * @param player
	 * @param row
	 * @param column
	 * @param opponent
	 * @return
	 */
	private String checkUpMove(char state[][], char player, int row,
			int column, char opponent) {
		boolean isValid = false;
		if (state[row - 1][column] == '*' || state[row - 1][column] == player) {
			// contains no valid move
			return null;
		} else {
			row--;
			while (row > 0) {
				row--;
				if (state[row][column] == player) {
					return null;
				} else if (state[row][column] == '*') {
					isValid = true;
					break;
				}
			}
		}
		if (isValid) {
			String move = numberAlphaMapping.get(column)
					+ Integer.toString(row + 1);
			return move;
		} else {
			return null;
		}
	}

	/**
	 * The method checks downwards for valid moves.
	 * 
	 * @param state
	 * @param player
	 * @param row
	 * @param column
	 * @param opponent
	 * @return
	 */
	private String checkDownMove(char state[][], char player, int row,
			int column, char opponent) {
		boolean isValid = false;
		if (state[row + 1][column] == '*' || state[row + 1][column] == player) {
			// contains no valid move
			return null;
		} else {
			row++;
			while (row < 7) {
				row++;
				if (state[row][column] == player) {
					return null;
				} else if (state[row][column] == '*') {
					isValid = true;
					break;
				}
			}
		}
		if (isValid) {
			String move = numberAlphaMapping.get(column)
					+ Integer.toString(row + 1);
			return move;
		} else {
			return null;
		}
	}

	/**
	 * The method checks the diagonal vertically up right positions for valid
	 * moves.
	 * 
	 * @param state
	 * @param player
	 * @param row
	 * @param column
	 * @param opponent
	 * @return
	 */
	private String checkRightUpDiagonal(char state[][], char player, int row,
			int column, char opponent) {
		boolean isValid = false;
		if (row - 1 >= 0 && column + 1 <= 7) {
			if (state[row - 1][column + 1] == '*'
					|| state[row - 1][column + 1] == player) {
				return null;
			} else {
				row--;
				column++;
				while (row > 0 && column < 7) {
					row--;
					column++;
					if (state[row][column] == player) {
						return null;
					} else if (state[row][column] == '*') {
						isValid = true;
						break;
					}
				}
			}
		}
		if (isValid) {
			String move = numberAlphaMapping.get(column)
					+ Integer.toString(row + 1);
			return move;
		} else {
			return null;
		}
	}

	/**
	 * The method checks the vertically up left positions for valid moves.
	 * 
	 * @param state
	 * @param player
	 * @param row
	 * @param column
	 * @param opponent
	 * @return
	 */
	private String checkLeftUpDiagonal(char state[][], char player, int row,
			int column, char opponent) {
		boolean isValid = false;
		if (row - 1 >= 0 && column - 1 >= 0) {
			if (state[row - 1][column - 1] == '*'
					|| state[row - 1][column - 1] == player) {
				return null;
			} else {
				row--;
				column--;
				while (row > 0 && column > 0) {
					row--;
					column--;
					if (state[row][column] == player) {
						return null;
					} else if (state[row][column] == '*') {
						isValid = true;
						break;
					}
				}
			}
		}
		if (isValid) {
			String move = numberAlphaMapping.get(column)
					+ Integer.toString(row + 1);
			return move;
		} else {
			return null;
		}
	}

	/**
	 * The method checks the vertically down right positions for valid moves.
	 * 
	 * @param state
	 * @param player
	 * @param row
	 * @param column
	 * @param opponent
	 * @return
	 */
	private String checkRightDownDiagonal(char state[][], char player, int row,
			int column, char opponent) {
		boolean isValid = false;
		if (row + 1 <= 7 && column + 1 <= 7) {
			if (state[row + 1][column + 1] == '*'
					|| state[row + 1][column + 1] == player) {
				return null;
			} else {
				row++;
				column++;
				while (row < 7 && column < 7) {
					row++;
					column++;
					if (state[row][column] == player) {
						return null;
					} else if (state[row][column] == '*') {
						isValid = true;
						break;
					}
				}
			}
		}
		if (isValid) {
			String move = numberAlphaMapping.get(column)
					+ Integer.toString(row + 1);
			return move;
		} else {
			return null;
		}
	}

	/**
	 * The method checks the vertically down left positions for valid moves.
	 * 
	 * @param state
	 * @param player
	 * @param row
	 * @param column
	 * @param opponent
	 * @return
	 */
	private String checkLeftDownDiagonal(char state[][], char player, int row,
			int column, char opponent) {
		boolean isValid = false;
		if (row + 1 <= 7 && column - 1 >= 0) {
			if (state[row + 1][column - 1] == '*'
					|| state[row + 1][column - 1] == player) {
				return null;
			} else {
				row++;
				column--;
				while (row < 7 && column > 0) {
					row++;
					column--;
					if (state[row][column] == player) {
						return null;
					} else if (state[row][column] == '*') {
						isValid = true;
						break;
					}
				}
			}
		}
		if (isValid) {
			String move = numberAlphaMapping.get(column)
					+ Integer.toString(row + 1);
			return move;
		} else {
			return null;
		}
	}

	/**
	 * The method checks the diagonals for valid moves.
	 * 
	 * @param state
	 * @param player
	 * @param legalMoves
	 * @param row
	 * @param column
	 * @param opponent
	 */
	private void checkDiagonals(char state[][], char player,
			List<String> legalMoves, int row, int column, char opponent) {
		if (row != 0) {
			String rightUpMove = checkRightUpDiagonal(state, player, row,
					column, opponent);
			addMove(legalMoves, rightUpMove);

			String leftUpMove = checkLeftUpDiagonal(state, player, row, column,
					opponent);
			addMove(legalMoves, leftUpMove);
		}

		if (row != 7) {

			String rightDownMove = checkRightDownDiagonal(state, player, row,
					column, opponent);
			addMove(legalMoves, rightDownMove);

			String leftDownMove = checkLeftDownDiagonal(state, player, row,
					column, opponent);
			addMove(legalMoves, leftDownMove);

		}
	}

	/**
	 * The method checks for valid/legal moves.
	 * 
	 * @param state
	 * @param player
	 * @param opponent
	 * @return
	 */
	private List<String> legalMoves(char state[][], char player, char opponent) {
		List<String> legalMoves = new ArrayList<String>();
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				if (state[i][j] == player) {
					if (j != 0) {
						// check the left horizontal for legal moves available
						String leftMove = checkLeftHorizontal(state, player, i,
								j, opponent);
						addMove(legalMoves, leftMove);
					}

					if (j != 7) {
						// check the right horizontal for legal moves available
						String rightMove = checkRightHorizontal(state, player,
								i, j, opponent);
						addMove(legalMoves, rightMove);
					}

					if (i != 0) {
						// check vertically up for legal moves available
						String upMove = checkUpMove(state, player, i, j,
								opponent);
						addMove(legalMoves, upMove);
					}

					if (i != 7) {
						// check vertically down for legal moves available
						String downMove = checkDownMove(state, player, i, j,
								opponent);
						addMove(legalMoves, downMove);
					}

					// check diagonal positions for legal moves available
					checkDiagonals(state, player, legalMoves, i, j, opponent);
				}
			}
		}

		return legalMoves;
	}

	/**
	 * The method returns the position up to which the pieces need to be flipped
	 * on the board on the left horizontal.
	 * 
	 * @param newState
	 * @param row
	 * @param column
	 * @param player
	 * @return
	 */
	private int[] leftHorizontalFlipPosition(char newState[][], int row,
			int column, char player) {
		column--;
		boolean isValid = false;
		int position[] = new int[2];
		if (newState[row][column] == '*' || newState[row][column] == player) {
			return null;
		} else {
			column--;
			while (column >= 0) {
				if (newState[row][column] == player) {
					isValid = true;
					position[0] = row;
					position[1] = column;
					break;
				} else if (newState[row][column] == '*') {
					isValid = false;
					break;
				} else {
					column--;
				}
			}
		}
		if (isValid) {
			return position;
		} else {
			return null;
		}
	}

	/**
	 * The method returns the position up to which the pieces need to be flipped
	 * on the board on the right horizontal.
	 * 
	 * @param newState
	 * @param row
	 * @param column
	 * @param player
	 * @return
	 */
	private int[] rightHorizontalFlipPosition(char newState[][], int row,
			int column, char player) {
		column++;
		boolean isValid = false;
		int position[] = new int[2];
		if (newState[row][column] == '*' || newState[row][column] == player) {
			return null;
		} else {
			column++;
			while (column <= 7) {
				if (newState[row][column] == player) {
					isValid = true;
					position[0] = row;
					position[1] = column;
					break;
				} else if (newState[row][column] == '*') {
					isValid = false;
					break;
				} else {
					column++;
				}
			}
		}
		if (isValid) {
			return position;
		} else {
			return null;
		}
	}

	/**
	 * The method returns the position up to which the pieces need to be flipped
	 * on the board vertically upwards.
	 * 
	 * @param newState
	 * @param row
	 * @param column
	 * @param player
	 * @return
	 */
	private int[] upFlipPosition(char newState[][], int row, int column,
			char player) {
		row--;
		boolean isValid = false;
		int position[] = new int[2];
		if (newState[row][column] == '*' || newState[row][column] == player) {
			return null;
		} else {
			row--;
			while (row >= 0) {
				if (newState[row][column] == player) {
					isValid = true;
					position[0] = row;
					position[1] = column;
					break;
				} else if (newState[row][column] == '*') {
					isValid = false;
					break;
				} else {
					row--;
				}
			}
		}
		if (isValid) {
			return position;
		} else {
			return null;
		}
	}

	/**
	 * The method returns the position up to which the pieces need to be flipped
	 * on the board vertically downwards.
	 * 
	 * @param newState
	 * @param row
	 * @param column
	 * @param player
	 * @return
	 */
	private int[] downFlipPosition(char newState[][], int row, int column,
			char player) {
		row++;
		boolean isValid = false;
		int position[] = new int[2];
		if (newState[row][column] == '*' || newState[row][column] == player) {
			return null;
		} else {
			row++;
			while (row <= 7) {
				if (newState[row][column] == player) {
					isValid = true;
					position[0] = row;
					position[1] = column;
					break;
				} else if (newState[row][column] == '*') {
					isValid = false;
					break;
				} else {
					row++;
				}
			}
		}
		if (isValid) {
			return position;
		} else {
			return null;
		}
	}

	/**
	 * The method returns the position up to which the pieces need to be flipped
	 * on the board diagonally upwards.
	 * 
	 * @param newState
	 * @param row
	 * @param column
	 * @param player
	 * @return
	 */
	private int[] rightUpFlipPosition(char newState[][], int row, int column,
			char player) {
		column++;
		row--;
		boolean isValid = false;
		int position[] = new int[2];

		if (row >= 0 && column <= 7) {
			if (newState[row][column] == '*' || newState[row][column] == player) {
				return null;
			} else {
				column++;
				row--;
				while (column <= 7 && row >= 0) {
					if (newState[row][column] == player) {
						isValid = true;
						position[0] = row;
						position[1] = column;
						break;
					} else if (newState[row][column] == '*') {
						isValid = false;
						break;
					} else {
						column++;
						row--;
					}
				}
			}
		}
		if (isValid) {
			return position;
		} else {
			return null;
		}
	}

	/**
	 * The method returns the position up to which the pieces need to be flipped
	 * on the board diagonally upwards.
	 * 
	 * @param newState
	 * @param row
	 * @param column
	 * @param player
	 * @return
	 */
	private int[] leftUpFlipPosition(char newState[][], int row, int column,
			char player) {
		column--;
		row--;
		boolean isValid = false;
		int position[] = new int[2];
		if (row >= 0 && column >= 0) {
			if (newState[row][column] == '*' || newState[row][column] == player) {
				return null;
			} else {
				column--;
				row--;
				while (column >= 0 && row >= 0) {
					if (newState[row][column] == player) {
						isValid = true;
						position[0] = row;
						position[1] = column;
						break;
					} else if (newState[row][column] == '*') {
						isValid = false;
						break;
					} else {
						column--;
						row--;
					}
				}
			}
		}
		if (isValid) {
			return position;
		} else {
			return null;
		}
	}

	/**
	 * The method returns the position up to which the pieces need to be flipped
	 * on the board diagonally downwards.
	 * 
	 * @param newState
	 * @param row
	 * @param column
	 * @param player
	 * @return
	 */
	private int[] leftDownFlipPosition(char newState[][], int row, int column,
			char player) {
		column--;
		row++;
		boolean isValid = false;
		int position[] = new int[2];
		if (row <= 7 && column >= 0) {
			if (newState[row][column] == '*' || newState[row][column] == player) {
				return null;
			} else {
				column--;
				row++;
				while (column >= 0 && row <= 7) {
					if (newState[row][column] == player) {
						isValid = true;
						position[0] = row;
						position[1] = column;
						break;
					} else if (newState[row][column] == '*') {
						isValid = false;
						break;
					} else {
						column--;
						row++;
					}
				}
			}
		}
		if (isValid) {
			return position;
		} else {
			return null;
		}
	}

	/**
	 * The method returns the position up to which the pieces need to be flipped
	 * on the board diagonally downwards.
	 * 
	 * @param newState
	 * @param row
	 * @param column
	 * @param player
	 * @return
	 */
	private int[] rightDownFlipPosition(char newState[][], int row, int column,
			char player) {
		column++;
		row++;
		boolean isValid = false;
		int position[] = new int[2];
		if (row <= 7 && column <= 7) {
			if (newState[row][column] == '*' || newState[row][column] == player) {
				return null;
			} else {
				column++;
				row++;
				while (column <= 7 && row <= 7) {
					if (newState[row][column] == player) {
						isValid = true;
						position[0] = row;
						position[1] = column;
						break;
					} else if (newState[row][column] == '*') {
						isValid = false;
						break;
					} else {
						column++;
						row++;
					}
				}
			}
		}
		if (isValid) {
			return position;
		} else {
			return null;
		}
	}

	private void rightUpFlip(char newState[][], int row, int column,
			int rightUpPosition[], char player) {
		while (row > rightUpPosition[0] && column < rightUpPosition[1]) {
			row--;
			column++;
			newState[row][column] = player;
		}
	}

	private void leftUpFlip(char newState[][], int row, int column,
			int rightUpPosition[], char player) {
		while (row > rightUpPosition[0] && column > rightUpPosition[1]) {
			row--;
			column--;
			newState[row][column] = player;
		}
	}

	private void leftDownFlip(char newState[][], int row, int column,
			int rightUpPosition[], char player) {
		while (row < rightUpPosition[0] && column > rightUpPosition[1]) {
			row++;
			column--;
			newState[row][column] = player;
		}
	}

	private void rightDownFlip(char newState[][], int row, int column,
			int rightUpPosition[], char player) {
		while (row < rightUpPosition[0] && column < rightUpPosition[1]) {
			row++;
			column++;
			newState[row][column] = player;
		}
	}

	private void diagonalFlip(char newState[][], int row, int column,
			char player) {
		int rightUpPosition[] = new int[2];
		int leftUpPosition[] = new int[2];
		int rightDownPosition[] = new int[2];
		int leftDownPosition[] = new int[2];

		if (row != 0) {
			rightUpPosition = rightUpFlipPosition(newState, row, column, player);

			if (rightUpPosition != null) {
				rightUpFlip(newState, row, column, rightUpPosition, player);
			}

			leftUpPosition = leftUpFlipPosition(newState, row, column, player);

			if (leftUpPosition != null) {
				leftUpFlip(newState, row, column, leftUpPosition, player);
			}

		}

		if (row != 7) {
			rightDownPosition = rightDownFlipPosition(newState, row, column,
					player);

			if (rightDownPosition != null) {
				rightDownFlip(newState, row, column, rightDownPosition, player);
			}

			leftDownPosition = leftDownFlipPosition(newState, row, column,
					player);

			if (leftDownPosition != null) {
				leftDownFlip(newState, row, column, leftDownPosition, player);
			}

		}
	}

	private void leftFlip(char newState[][], int row, int column,
			int leftFlipPosition[], char player) {
		while (column > leftFlipPosition[1]) {
			column--;
			newState[row][column] = player;
		}
	}

	private void rightFlip(char newState[][], int row, int column,
			int rightFlipPosition[], char player) {
		while (column < rightFlipPosition[1]) {
			column++;
			newState[row][column] = player;
		}
	}

	private void upFlip(char newState[][], int row, int column,
			int upFlipPosition[], char player) {
		while (row > upFlipPosition[0]) {
			row--;
			newState[row][column] = player;
		}
	}

	private void downFlip(char newState[][], int row, int column,
			int downFlipPosition[], char player) {
		while (row < downFlipPosition[0]) {
			row++;
			newState[row][column] = player;
		}
	}

	/**
	 * The method returns the new state after applying the next move to the
	 * current state.
	 * 
	 * @param currentState
	 * @param nextMove
	 * @param player
	 * @return
	 */
	private char[][] setUpNewState(char currentState[][], String nextMove,
			char player) {
		char newState[][] = new char[8][8];
		copyState(newState, currentState);
		int row = 0;
		int column = 0;
		int leftFlipPosition[] = new int[2];
		int rightFlipPosition[] = new int[2];
		int upFlipPosition[] = new int[2];
		int downFlipPosition[] = new int[2];

		column = alphaNumberMapping.get(nextMove.charAt(0));
		row = Character.getNumericValue(nextMove.charAt(1)) - 1;

		newState[row][column] = player;

		if (column != 0) {
			// get the position up to which we need to flip positions
			leftFlipPosition = leftHorizontalFlipPosition(newState, row,
					column, player);

			if (leftFlipPosition != null) {
				// flip the required pieces
				leftFlip(newState, row, column, leftFlipPosition, player);
			}
		}

		if (column != 7) {
			// get the position up to which we need to flip positions
			rightFlipPosition = rightHorizontalFlipPosition(newState, row,
					column, player);

			if (rightFlipPosition != null) {
				// flip the required pieces
				rightFlip(newState, row, column, rightFlipPosition, player);
			}
		}

		if (row != 0) {
			// get the position up to which we need to flip positions
			upFlipPosition = upFlipPosition(newState, row, column, player);

			if (upFlipPosition != null) {
				// flip the required pieces
				upFlip(newState, row, column, upFlipPosition, player);
			}
		}

		if (row != 7) {
			// get the position up to which we need to flip positions
			downFlipPosition = downFlipPosition(newState, row, column, player);

			if (downFlipPosition != null) {
				// flip the required pieces
				downFlip(newState, row, column, downFlipPosition, player);
			}
		}

		// flip pieces diagonally.
		diagonalFlip(newState, row, column, player);

		return newState;
	}

	/**
	 * The method returns the evaluation of the given state for the given
	 * player.
	 * 
	 * @param state
	 * @param player
	 * @return
	 */
	private int evaluationMethod(char state[][], char player) {
		int playerWeight = 0;
		int opponentWeight = 0;

		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				if (state[i][j] != '*') {
					if (state[i][j] == player) {
						playerWeight += positionalWeights[i][j];
					} else {
						opponentWeight += positionalWeights[i][j];
					}
				}
			}
		}
		return (playerWeight - opponentWeight);
	}

	/**
	 * The method returns the evaluation of the given state for the given player
	 * using a greedy approach. It returns the number of pieces the player has
	 * on the board.
	 * 
	 * @param state
	 * @param player
	 * @return
	 */
	private int greedyEvaluationMethod(char state[][], char player) {
		int playerWeight = 0;

		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				if (state[i][j] == player) {
					playerWeight++;
				}
			}
		}
		return playerWeight;
	}

	private void copyState(char destinationState[][], char sourceState[][]) {
		for (int i = 0; i < sourceState.length; i++) {
			for (int j = 0; j < sourceState[i].length; j++) {
				destinationState[i][j] = sourceState[i][j];
			}
		}
	}

	/**
	 * The method implements the greedy method for playing the game.
	 * 
	 * @param state
	 * @param player
	 * @param opponent
	 */
	public void greedyGamePlaying(char state[][], char player, char opponent) {
		List<String> legalMoves = new ArrayList<String>();
		legalMoves = legalMoves(state, player, opponent);
		List<Node> evaluatedNodes = new ArrayList<Node>();
		char currentState[][] = new char[8][8];

		copyState(currentState, state);

		if (legalMoves.size() != 0) {
			for (String newMove : legalMoves) {
				// set up new state with the new move.
				char newState[][] = setUpNewState(currentState, newMove, player);

				// evaluate the new state.
				int eval = greedyEvaluationMethod(newState, player);
				// int eval = evaluationMethod(newState, player);

				Node node = new Node();
				node.setName(newMove);
				node.setValue(eval);

				evaluatedNodes.add(node);

				copyState(currentState, state);
			}

			Collections.sort(evaluatedNodes, new Comparator<Node>() {
				@Override
				public int compare(Node o1, Node o2) {
					if (o1.getValue() != o2.getValue()) {
						return (o2.getValue() - o1.getValue());
					} else {
						int row1 = o1.getName().charAt(1);
						int column1 = alphaNumberMapping.get(o1.getName()
								.charAt(0));
						int row2 = o2.getName().charAt(1);
						int column2 = alphaNumberMapping.get(o2.getName()
								.charAt(0));

						if (row1 != row2) {
							return row1 - row2;
						} else {
							return column1 - column2;
						}
					}
				}
			});

		} else {

			// no valid moves (pass state)
		}

		// write output file with the decided move.
		writeOutputFile(evaluatedNodes.get(0).getName());
	}

	private void sortEvaluatedNodesMax(List<Node> evalNodes) {
		Collections.sort(evalNodes, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				if (o1.getValue() != o2.getValue()) {
					return (o2.getValue() - o1.getValue());
				} else {
					int row1 = o1.getName().charAt(1);
					int column1 = alphaNumberMapping
							.get(o1.getName().charAt(0));
					int row2 = o2.getName().charAt(1);
					int column2 = alphaNumberMapping
							.get(o2.getName().charAt(0));

					if (row1 != row2) {
						return row1 - row2;
					} else {
						return column1 - column2;
					}
				}
			}
		});
	}

	private void sortEvaluatedNodesMin(List<Node> evalNodes) {
		Collections.sort(evalNodes, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				if (o1.getValue() != o2.getValue()) {
					return (o1.getValue() - o2.getValue());
				} else {
					int row1 = o1.getName().charAt(1);
					int column1 = alphaNumberMapping
							.get(o1.getName().charAt(0));
					int row2 = o2.getName().charAt(1);
					int column2 = alphaNumberMapping
							.get(o2.getName().charAt(0));

					if (row1 != row2) {
						return row1 - row2;
					} else {
						return column1 - column2;
					}
				}
			}
		});
	}

	/**
	 * The method implements the Alpha Beta Pruning method for playing the game.
	 * 
	 * @param initialState
	 * @param player
	 * @param opponent
	 * @param cutOffDepth
	 */
	public void alphaBetaSearch(char initialState[][], char player,
			char opponent, int cutOffDepth) {
		char state[][] = new char[8][8];

		copyState(state, initialState);

		// get the legal moves according to the current state.
		List<String> actions = legalMoves(state, player, opponent);

		double alpha = Double.POSITIVE_INFINITY * -1;
		double beta = Double.POSITIVE_INFINITY;
		int depth = 0;
		List<Node> evaluatedNodes = new ArrayList<Node>();

		if (isGameOver(initialState)) {
			// writeAlphaBetaOutputFile(initialState, log);
			return;
		}

		if (actions.size() != 0) {
			for (String action : actions) {
				char newState[][] = setUpNewState(state, action, player);
				int val = minAlphaBetaValue(newState, opponent, player,
						depth + 1, cutOffDepth, action, alpha, beta, false);

				Node node = new Node(action, val, depth + 1);
				evaluatedNodes.add(node);
				sortEvaluatedNodesMax(evaluatedNodes);

				int currentMaxVal = evaluatedNodes.get(0).getValue();

				alpha = currentMaxVal;

				copyState(state, initialState);
			}

			sortEvaluatedNodesMax(evaluatedNodes);

		} else {

			// no valid moves available (pass state).

		}

		// write output file with the decided move.
		writeOutputFile(evaluatedNodes.get(0).getName());
	}

	private int maxAlphaBetaValue(char state[][], char player, char opponent,
			int depth, int cutOffDepth, String action, double alpha,
			double beta, boolean isPreviousPass) {
		char currentState[][] = new char[8][8];
		copyState(currentState, state);

		if (depth == cutOffDepth || isGameOver(currentState)) {
			// evaluate the current state.
			int evaluation = evaluationMethod(currentState, player);
			return evaluation;
		}

		List<String> actions = new ArrayList<String>();
		List<Node> evaluatedNodes = new ArrayList<Node>();

		actions = legalMoves(state, player, opponent);

		if (actions.size() != 0) {
			for (String nextAction : actions) {
				char newState[][] = setUpNewState(currentState, nextAction,
						player);

				int val = 0;

				if (action.equalsIgnoreCase("pass")) {
					val = minAlphaBetaValue(newState, opponent, player,
							depth + 1, cutOffDepth, nextAction, alpha, beta,
							true);
				} else {
					val = minAlphaBetaValue(newState, opponent, player,
							depth + 1, cutOffDepth, nextAction, alpha, beta,
							false);
				}

				Node node = new Node(nextAction, val, depth + 1);
				evaluatedNodes.add(node);
				sortEvaluatedNodesMax(evaluatedNodes);

				int currentMaxVal = evaluatedNodes.get(0).getValue();

				if (currentMaxVal >= beta) {
					return currentMaxVal;
				}

				if (currentMaxVal > alpha) {
					alpha = currentMaxVal;
				}

				copyState(currentState, state);
			}
		} else {
			char newState[][] = new char[8][8];
			copyState(newState, currentState);
			int val = 0;
			if (!isPreviousPass) {
				if (action.equalsIgnoreCase("pass")) {
					val = minAlphaBetaValue(newState, opponent, player,
							depth + 1, cutOffDepth, "pass", alpha, beta, true);
				} else {
					val = minAlphaBetaValue(newState, opponent, player,
							depth + 1, cutOffDepth, "pass", alpha, beta, false);
				}

				if (val >= beta) {
					return val;
				}

				alpha = val;

				return val;

			} else {
				val = evaluationMethod(currentState, player);
				return val;
			}
		}

		sortEvaluatedNodesMax(evaluatedNodes);

		return evaluatedNodes.get(0).getValue();
	}

	private int minAlphaBetaValue(char state[][], char player, char opponent,
			int depth, int cutOffDepth, String action, double alpha,
			double beta, boolean isPreviousPass) {
		char currentState[][] = new char[8][8];
		copyState(currentState, state);

		if (depth == cutOffDepth || isGameOver(currentState)) {
			// evaluate the current state.
			int evaluation = evaluationMethod(currentState, opponent);
			return evaluation;
		}

		List<String> actions = new ArrayList<String>();
		List<Node> evaluatedNodes = new ArrayList<Node>();

		// get the legal moves.
		actions = legalMoves(state, player, opponent);

		if (actions.size() != 0) {
			for (String nextAction : actions) {
				char newState[][] = setUpNewState(currentState, nextAction,
						player);

				int val = 0;

				if (action.equalsIgnoreCase("pass")) {
					val = maxAlphaBetaValue(newState, opponent, player,
							depth + 1, cutOffDepth, nextAction, alpha, beta,
							true);
				} else {
					val = maxAlphaBetaValue(newState, opponent, player,
							depth + 1, cutOffDepth, nextAction, alpha, beta,
							false);
				}

				Node node = new Node(nextAction, val, depth + 1);
				evaluatedNodes.add(node);
				sortEvaluatedNodesMin(evaluatedNodes);

				int currentMinVal = evaluatedNodes.get(0).getValue();

				if (currentMinVal <= alpha) {
					return currentMinVal;
				}

				if (currentMinVal < beta) {
					beta = currentMinVal;
				}

				copyState(currentState, state);
			}
		} else {
			char newState[][] = new char[8][8];
			copyState(newState, currentState);
			int val = 0;
			if (!isPreviousPass) {
				if (action.equalsIgnoreCase("pass")) {
					val = maxAlphaBetaValue(newState, opponent, player,
							depth + 1, cutOffDepth, "pass", alpha, beta, true);
				} else {
					val = maxAlphaBetaValue(newState, opponent, player,
							depth + 1, cutOffDepth, "pass", alpha, beta, false);
				}

				if (val <= alpha) {
					return val;
				}

				beta = val;

				return val;

			} else {
				val = evaluationMethod(currentState, opponent);
				return val;
			}
		}

		sortEvaluatedNodesMin(evaluatedNodes);

		return evaluatedNodes.get(0).getValue();
	}

	/**
	 * The method writes the output file with the decided move.
	 * 
	 * @param finalState
	 */
	private void writeOutputFile(String finalMove) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"output.txt"));
			writer.write(finalMove);
			writer.close();
		} catch (IOException ioException) {
			System.err.format("IO Exception: " + ioException);
		}
	}

	/**
	 * The method checks whether the game is over by evaluating the board.
	 * 
	 * @param state
	 * @return
	 */
	private boolean isGameOver(char state[][]) {

		boolean hasEmptySquare = false;
		boolean hasXPiece = false;
		boolean hasOPiece = false;

		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				if (state[i][j] == '*') {
					hasEmptySquare = true;
				}

				if (state[i][j] == 'X') {
					hasXPiece = true;
				}

				if (state[i][j] == 'O') {
					hasOPiece = true;
				}
			}
		}

		// no empty square means game ended.
		if (!hasEmptySquare) {
			return true;
		} else if (!hasXPiece || !hasOPiece) {
			return true;
		} else {
			return false;
		}

	}

	public static void main(String[] args) {

		Agent gameAgent = new Agent();

		// read the input file to get the current board state and other
		// parameters.
		gameAgent.readInputFile();

		switch (gameAgent.getTask()) {
		case 4:
			if (gameAgent.getProcessingTimeLeft() > 10) {
				// use alpha beta pruning when more time is available.
				gameAgent.setCutOffDepth(5);
				gameAgent.alphaBetaSearch(gameAgent.getInitialState(),
						gameAgent.getPlayer(), gameAgent.getPlayer(),
						gameAgent.getCutOffDepth());
			} else {
				// resort to greedy strategy when time remaining is less.
				gameAgent.greedyGamePlaying(gameAgent.getInitialState(),
						gameAgent.getPlayer(), gameAgent.getOpponent());
			}
			break;
		}

	}
}
