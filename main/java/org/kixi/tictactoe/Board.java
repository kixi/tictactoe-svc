package org.kixi.tictactoe;

public class Board {

    private TicTacToe.Player[][] board = new TicTacToe.Player[3][3];

    public void setPosition(TicTacToe.Player player, int row, int col) {
        if (this.board[row][col] != null) {
            throw new RuntimeException("cell occupied");
        }
        this.board[row][col]=player;
    }

    public TicTacToe.Player getWinner() {
        for (TicTacToe.Player p: TicTacToe.Player.values()) {
            for (int i = 0; i< 3; i++) {
                int pcnt = 0;
                for (int j=0; j<3; j++) {
                    if (board[i][j] == p) {
                        ++pcnt;
                    }
                }
                if (pcnt == 3) {
                    return p;
                }
            }

            for (int i = 0; i< 3; i++) {
                int pcnt = 0;
                for (int j=0; j<3; j++) {
                    if (board[j][i] == p) {
                        ++pcnt;
                    }
                }
                if (pcnt == 3) {
                    return p;
                }
            }
            int pcnt = 0;
            for (int i=0; i<3; i++ ) {
                if (board [i][i]==p) {
                    ++pcnt;
                }
            }
            if (pcnt == 3) {
                return p;
            }
            pcnt = 0;
            for (int i=0; i<3; i++ ) {
                if (board [i][2-i]==p) {
                    ++pcnt;
                }
            }
            if (pcnt == 3) {
                return p;
            }
        }
        return null;
    }

    public void renderBoard(BoardVisitor visitor) {
        for (int i = 0; i < 3; i++) {
            for (int j=0; j<3; j++) {
                visitor.setPosition(this.board[i][j], i, j);
            }
        };
    }

}
