-- :name get-game :? :1
-- :doc retrieves a tictactoe game with the corresponding id
SELECT id, c00, c01, c02, c10, c11, c12, c20, c21, c22, turn, winner FROM TICTACTOE
WHERE id = :id

-- :name get-games :? :*
select id from tictactoe 

-- :name update-game :! :n
update tictactoe
set c00 = :c00,
    c01 = :c01,
    c02 = :c02,
    c10 = :c10,
    c11 = :c11,
    c12 = :c12,
    c20 = :c20,
    c21 = :c21,
    c22 = :c22,
    turn = :turn,
    winner = :winner
where
    id = :id

-- :name insert-game :! :n
insert into tictactoe (id,c00,c01,c02,c10,c11,c12,c20,c21,c22,turn,winner)
values (:id,:c00,:c01,:c02,:c10,:c11,:c12,:c20,:c21,:c22,:turn,:winner)

