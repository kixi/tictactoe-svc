package org.kixi.tictactoe;

public interface BoardVisitor {
    void setPosition(TicTacToe.Player player, int row, int col);
}
