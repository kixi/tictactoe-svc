package org.kixi.tictactoe;

public class TicTacToe {

    public enum Player {X, Y};

    private Player turn;

    private Board board;


    public TicTacToe(Player startingPlayer) {
        this.turn = startingPlayer;
        this.board = new Board();
    }

    public void makeMove(Player player, int row, int col) {
        if (!this.turn.equals(player)) {
            throw new RuntimeException("turn");
        }

        this.board.setPosition(player, row, col);

        if (player == Player.X) {
            this.turn = Player.Y;
        } else {
            this.turn = Player.X;
        }

    }

    public void renderBoard(BoardVisitor visitor) {
        this.board.renderBoard(visitor);
    }

    public Player getTurn() {
        return turn;
    }

    public Player getWinner() {
        return this.board.getWinner();
    }
}
