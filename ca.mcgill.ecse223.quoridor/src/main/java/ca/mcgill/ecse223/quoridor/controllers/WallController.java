package ca.mcgill.ecse223.quoridor.controllers;

import ca.mcgill.ecse223.quoridor.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.WallGraph;
import ca.mcgill.ecse223.quoridor.model.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WallController {

    /**
     * @author Tritin Truong
     * Set the tile of WallMove to a specific row and column
     * returns true if operation is successful
     *
     * @param row The target row of the move
     * @param col The target column of the move
     * @return Outcome of operation either
     * @throws UnsupportedOperationException
     */
    public static Boolean moveWall(int row, int col) throws UnsupportedOperationException{
        WallMove move = ModelQuery.getWallMoveCandidate();
        if(row<1 || row > 8 || col < 1 || col > 8){
            return false;
        }
        Tile target = ModelQuery.getTile(row,col);
        move.setTargetTile(target);
        return true;
    }

    /**
     * @author Tritin Truong
     * Shifts the position of a WallMove 1 tile left,right,up or down
     * returns true if the operation is successful
     *
     * @param side Direction of the shift (left,right,down,up
     * @return outcome of operation
     * @throws UnsupportedOperationException
     */
    public static Boolean shiftWall(String side) throws UnsupportedOperationException {
        WallMove move = ModelQuery.getWallMoveCandidate();
        int row = move.getTargetTile().getRow();
        int col = move.getTargetTile().getColumn();
        switch(side){
            case "left":{
                col-=1;
                break;
            }
            case "right":{
                col+=1;
                break;
            }
            case "up": {
                row-=1;
                break;
            }
            case "down":{
                row+=1;
                break;
            }
        }
        return WallController.moveWall(row,col);
    }

    /**
     * @author Tritin Truong
     *
     * Attempts to drop the wall at the current position
     * Also completes the moves
     *
     * @return outcome of operation
     * @throws UnsupportedOperationException
     */
    public static Boolean dropWall() throws UnsupportedOperationException{
        WallMove move = ModelQuery.getWallMoveCandidate();
        Player player = ModelQuery.getPlayerToMove();

        // validate no overlap
        if(!ValidatePositionController.validateWallPosition(move.getTargetTile().getRow(),move.getTargetTile().getColumn(),move.getWallDirection())){
            return false;
        }

        int numberOfPaths = pathExistsForPlayers().size();
        if(numberOfPaths<2 || (ModelQuery.isFourPlayer() && numberOfPaths<4)){
            return false;
        }

        ModelQuery.getCurrentGame().addMove(move);
        ModelQuery.getCurrentGame().setWallMoveCandidate(null);




        if(player.equals(ModelQuery.getWhitePlayer())) {
            ModelQuery.getCurrentGame().getCurrentPosition().addWhiteWallsOnBoard(move.getWallPlaced());
            SwitchPlayerController.switchActivePlayer();
        }else if(player.equals(ModelQuery.getBlackPlayer())){
            ModelQuery.getCurrentGame().getCurrentPosition().addBlackWallsOnBoard(move.getWallPlaced());
            SwitchPlayerController.switchActivePlayer();
        }else if(player.equals(ModelQuery.getRedPlayer())) {
        	ModelQuery.getCurrentGame().getCurrentPosition().addRedWallsOnBoard(move.getWallPlaced());
        	SwitchPlayerController.switchActivePlayer();
        }else {
        	ModelQuery.getCurrentGame().getCurrentPosition().addGreenWallsOnBoard(move.getWallPlaced());
        	SwitchPlayerController.switchActivePlayer();
        }
        return true;
    }

    public static boolean cancelWallMove(){
        Player player = ModelQuery.getPlayerToMove();
        WallMove move = ModelQuery.getWallMoveCandidate();
        if(move == null){
            return false;
        }
        if(player.equals(ModelQuery.getWhitePlayer())) {
            ModelQuery.getCurrentGame().getCurrentPosition().addWhiteWallsInStock(move.getWallPlaced());
        }else if(player.equals(ModelQuery.getBlackPlayer())){
            ModelQuery.getCurrentGame().getCurrentPosition().addBlackWallsInStock(move.getWallPlaced());
        }else if(player.equals(ModelQuery.getRedPlayer())){
            ModelQuery.getCurrentGame().getCurrentPosition().addRedWallsInStock(move.getWallPlaced());
        }else {
            ModelQuery.getCurrentGame().getCurrentPosition().addGreenWallsInStock(move.getWallPlaced());
        }
        ModelQuery.getCurrentGame().setWallMoveCandidate(null);
        return true;
    }

    /**
     * @author Kate Ward
     * Attempts to rotate wall in hand
     * returns true if successful
     *
     * @return outcome of operation
     * @throws UnsupportedOperationException
     */
    public static boolean rotateWall() {
    	Game game = QuoridorApplication.getQuoridor().getCurrentGame();
        if (game.getWallMoveCandidate().equals(null)) {
        	return false;
        }
        if( game.getWallMoveCandidate().getWallDirection() == Direction.Vertical) {
        	game.getWallMoveCandidate().setWallDirection(Direction.Horizontal);
        }
        else {
        	game.getWallMoveCandidate().setWallDirection(Direction.Vertical);
        }
    	
        return true;
    }

    /**
     * @author Kate Ward & Mark Zhu
     * Attempts to grab wall from stock and hold it in hand above board
     * returns true if successful
     *
     * @return outcome of operation
     * @throws UnsupportedOperationException
     */
    public static boolean grabWall() throws UnsupportedOperationException{
        Player player = ModelQuery.getPlayerToMove();
        int movesSize = ModelQuery.getMoves().size();
        int moveNum;
        int roundNum;
        if (movesSize>0) {
            roundNum = ModelQuery.getMoves().get(movesSize-1).getRoundNumber()+1;
            moveNum = roundNum/2;
        }
        else {
            moveNum = 0;
            roundNum = 0;
        }
        if (ModelQuery.getCurrentGame().getWallMoveCandidate()!=null) {		//wall already in hand
        	return false;
        }
        else {
    		if(player.equals(ModelQuery.getWhitePlayer()) && ModelQuery.getCurrentGame().getCurrentPosition().getWhiteWallsInStock().size()>0) {
        		List <Wall> walls = ModelQuery.getCurrentGame().getCurrentPosition().getWhiteWallsInStock();
        		Wall wall = walls.get(0);		//get(0) null for some reason
                ModelQuery.getCurrentGame().getCurrentPosition().removeWhiteWallsInStock(wall);
                //create wall move candidate
                WallMove move = new WallMove(moveNum, roundNum, player, ModelQuery.getTile(1,1), ModelQuery.getCurrentGame(), Direction.Vertical, wall);
                ModelQuery.getCurrentGame().setWallMoveCandidate(move);
                
            }else if(player.equals(ModelQuery.getBlackPlayer()) && ModelQuery.getCurrentGame().getCurrentPosition().getBlackWallsInStock().size()>0){
            	List <Wall> walls = ModelQuery.getCurrentGame().getCurrentPosition().getBlackWallsInStock();
            	Wall wall = walls.get(0);		//get(0) null for some reason
                ModelQuery.getCurrentGame().getCurrentPosition().removeBlackWallsInStock(wall);
                //create wall move candidate
                WallMove move = new WallMove(moveNum, roundNum, player, ModelQuery.getTile(1,1), ModelQuery.getCurrentGame(), Direction.Vertical, wall);
                ModelQuery.getCurrentGame().setWallMoveCandidate(move);
                
            //4player
            }else if(player.equals(ModelQuery.getRedPlayer()) && ModelQuery.getCurrentGame().getCurrentPosition().getRedWallsInStock().size()>0){
            	List <Wall> walls = ModelQuery.getCurrentGame().getCurrentPosition().getRedWallsInStock();
            	Wall wall = walls.get(0);		
                ModelQuery.getCurrentGame().getCurrentPosition().removeRedWallsInStock(wall);
                //create wall move candidate
                WallMove move = new WallMove(moveNum, roundNum, player, ModelQuery.getTile(1,1), ModelQuery.getCurrentGame(), Direction.Vertical, wall);
                ModelQuery.getCurrentGame().setWallMoveCandidate(move);
                
            }else if(player.equals(ModelQuery.getGreenPlayer()) && ModelQuery.getCurrentGame().getCurrentPosition().getGreenWallsInStock().size()>0){
            	List <Wall> walls = ModelQuery.getCurrentGame().getCurrentPosition().getGreenWallsInStock();
            	Wall wall = walls.get(0);		
                ModelQuery.getCurrentGame().getCurrentPosition().removeGreenWallsInStock(wall);
                //create wall move candidate
                WallMove move = new WallMove(moveNum, roundNum, player, ModelQuery.getTile(1,1), ModelQuery.getCurrentGame(), Direction.Vertical, wall);
                ModelQuery.getCurrentGame().setWallMoveCandidate(move);
            }
        	//no player has walls in stock
            else {
            	return false;
            }
    	}
    	
        return true;
    }

    public static void initGraph(){

        List<Wall> placedWalls = ModelQuery.getAllWallsOnBoard();
        WallGraph graph = new WallGraph();
        // create graph
        for(int i =0 ; i<81; i++){
            List<Integer> neighbors = getNeighbhors(i);
            for( int neighbor: neighbors) {
                graph.addEdge(i, neighbor);
            }
        }

        // add cuts (if the game was loaded in)
        for (Wall wall: placedWalls){
            int[][] cuts = getWallCuts(wall);
            graph.cutEdgeUndirected(cuts[0][0],cuts[0][1]);
            graph.cutEdgeUndirected(cuts[1][0],cuts[1][1]);
        }
        ModelQuery.getCurrentPosition().setWallGraph(graph);
    }

    public static void updateGraph(){
        WallGraph graph = ModelQuery.getCurrentPosition().getWallGraph();
        List<Wall> placedWalls = ModelQuery.getAllWallsOnBoard();
        for (Wall wall: placedWalls){
            int[][] cuts = getWallCuts(wall);
            graph.cutEdgeUndirected(cuts[0][0],cuts[0][1]);
            graph.cutEdgeUndirected(cuts[1][0],cuts[1][1]);
        }
    }

    private static List<Integer> getNeighbhors(int tile_index){
        int row = Math.floorDiv(tile_index, 9);
        int col = tile_index % 9;

        ArrayList<Integer> neighbhors = new ArrayList();
        if(row>0){
            neighbhors.add(9*(row-1)+col);
        }
        if(row<8){
            neighbhors.add(9*(row+1)+col);
        }
        if(col>0){
            neighbhors.add(9*row + col-1);
        }
        if(col<8){
            neighbhors.add(9*row + col+1);
        }
        return neighbhors;
    }

    private  static int[][] getWallCuts(Wall wall){
        int[][] cuts= new int[2][2];
        WallMove move = wall.getMove();
        int row = move.getTargetTile().getRow();
        int col = move.getTargetTile().getColumn();
        Direction dir = wall.getMove().getWallDirection();

        if(dir == Direction.Vertical){
            cuts[0][0] = coordToIndex(row,col);
            cuts[0][1] = coordToIndex(row,col+1);

            cuts[1][0] = coordToIndex(row+1,col);;
            cuts[1][1] = coordToIndex(row+1,col+1);;
        }
        else{
            cuts[0][0] = coordToIndex(row,col);;
            cuts[0][1] = coordToIndex(row+1,col);;

            cuts[1][0] = coordToIndex(row,col+1);;
            cuts[1][1] = coordToIndex(row+1,col+1);;
        }
        return cuts;
    }

    /**@author Tritin Truong
     * This method if the current wall move candidate blocks player paths given a certain wallMoveCandidate
     *
     * @return A list of players for which a path exists
     */
    public static List<Player> pathExistsForPlayers(){
        WallGraph graph = ModelQuery.getCurrentPosition().getWallGraph();
        List<PlayerPosition> playerList = ModelQuery.getAllPlayerPosition();
        List<Player> res = new ArrayList<>();
        updateGraph();

        // cut the wall graph at the position
        int[][] cuts = getWallCuts(ModelQuery.getWallMoveCandidate().getWallPlaced());
        graph.cutEdgeUndirected(cuts[0][0],cuts[0][1]);
        graph.cutEdgeUndirected(cuts[1][0],cuts[1][1]);

        GamePosition position = ModelQuery.getCurrentPosition();
        for(PlayerPosition pos: playerList){
            Destination dest = pos.getPlayer().getDestination();
            int tile_index = tileToIndex(pos.getTile());
            if(dest.getDirection().equals(Direction.Horizontal)){
                if(position.getWallGraph().reachesDest(tile_index, dest.getTargetNumber(), -1)>=0){
                    res.add(pos.getPlayer());
                }
            }else{
                if(position.getWallGraph().reachesDest(tile_index,-1, dest.getTargetNumber())>=0){
                    res.add(pos.getPlayer());
                }
            }
        }

        //remove the cut
        graph.addEdgeUndirected(cuts[0][0],cuts[0][1]);
        graph.addEdgeUndirected(cuts[1][0],cuts[1][1]);

        return res;
    }

    /**@author Tritin Truong
     *
     * This method return the optimal tile to go for the current player, in order to reach the end goal the fastest
     * @return The target Tile
     */
    public static String findOptimalPath(){
        Player player = ModelQuery.getPlayerToMove();
        WallGraph graph = ModelQuery.getCurrentPosition().getWallGraph();
        updateGraph();
        PlayerPosition position = ModelQuery.getPlayerPositionOfPlayerToMove();
        List<PlayerPosition> allPositions = ModelQuery.getAllPlayerPosition();

        // exclude the current tile
        allPositions.remove(position);

        //setup graph for the other players
        for(PlayerPosition pos: allPositions){
            addEdgesForPawn(graph, pos.getTile());
        }

        // get possible moves
        LinkedList<Integer> adjacents  = graph.getAdjacent(tileToIndex(position.getTile()));
        int optimal_index = adjacents.get(0);
        int shortest = Integer.MAX_VALUE;
        int distance =0;
        for(int tile_index : adjacents){
            distance = minimumDistance(graph,player.getDestination(), indexToTile(tile_index));
            if(distance<shortest){
                optimal_index = tile_index;
                shortest = distance;
            }
        }

        // Storing the optimal tile as a string
        Tile optimalTile =  indexToTile(optimal_index);
        int row = optimalTile.getRow();
        int col = optimalTile.getColumn();
        char columnLetter = (char) (col + 96);
        String bestTile = (columnLetter) + Integer.toString(row);

        System.out.println(bestTile);

        //resetting the graph for other players
        for(PlayerPosition pos: allPositions){
            removeEdgesForPawn(graph, pos.getTile());
        }

        return bestTile;
    }

    public static void updatePlayerDistances(){
        WallGraph wallGraph = ModelQuery.getCurrentPosition().getWallGraph();
        List<PlayerPosition> positions = ModelQuery.getAllPlayerPosition();
        updateGraph();
        // Storing the adjacency lists for each tile
        for(PlayerPosition position : positions){
            addEdgesForPawn(wallGraph, position.getTile());
        }

        int distance = 0;
        for(PlayerPosition pos: positions){
            Destination dest = pos.getPlayer().getDestination();
            distance = minimumDistance(wallGraph, dest, pos.getTile());
        }

        for(PlayerPosition position : positions){
            removeEdgesForPawn(wallGraph, position.getTile());
        }
    }

    private static int minimumDistance(WallGraph wallGraph, Destination dest, Tile tile) {
        int tile_index  = tileToIndex(tile);
        int distance;
        if(dest.getDirection().equals(Direction.Horizontal)){
            distance = wallGraph.reachesDest(tile_index, dest.getTargetNumber(), -1);
        }else{
            distance = wallGraph.reachesDest(tile_index,-1, dest.getTargetNumber());
        }
        return distance;
    }

    private static void removeEdgesForPawn(WallGraph wallGraph, Tile source) {
        LinkedList<Integer> adjacents =  wallGraph.getAdjacent(tileToIndex(source));
        for(int i = 0; i< adjacents.size(); i++){
            // re adding the neighbors
            wallGraph.addEdge(adjacents.get(i), tileToIndex(source));
            //removing the jumps
            for(int k = i+1; k<adjacents.size(); k++){
                wallGraph.cutEdgeUndirected(adjacents.get(i),adjacents.get(k));
            }
        }
    }

    private static void addEdgesForPawn(WallGraph wallGraph, Tile source) {
        LinkedList<Integer> adjacents =  wallGraph.getAdjacent(tileToIndex(source));
        for(int i = 0; i< adjacents.size(); i++){
            //stopping the neighbhors from going in
            wallGraph.cutEdge(adjacents.get(i), tileToIndex(source));
            // putting the jumps
            for(int k = i+1; k<adjacents.size(); k++){
                wallGraph.addEdgeUndirected(adjacents.get(i),adjacents.get(k));
            }
        }
    }

    private static Tile indexToTile(int index){
        return ModelQuery.getBoard().getTile(index);
    }

    private static int coordToIndex(int row,int col){
        return 9*(row-1)+col-1;
    }

    private static int tileToIndex(Tile tile){
        return 9*(tile.getRow()-1)+tile.getColumn()-1;
    }
}
