package ca.mcgill.ecse223.quoridor.view;
import ca.mcgill.ecse223.quoridor.controllers.ModelQuery;
import ca.mcgill.ecse223.quoridor.controllers.StartNewGameController;
import ca.mcgill.ecse223.quoridor.model.Player;
import javafx.animation.KeyFrame;

import javafx.animation.Timeline;
import javafx.event.ActionEvent;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import jdk.javadoc.internal.tool.Start;


public class InitializeBoardController extends ViewController{


    @FXML
    private AnchorPane board;
    Rectangle wall;
    public Text whitePlayerName;
    public Text blackPlayerName;
    public Text whitePlayerName1;
    public Text blackPlayerName1;
    public Label timerForWhitePlayer;
    public Label timerForBlackPlayer;
    public Text whiteNumOfWalls;
    public Text blackNumOfWalls;
    public Timeline timeline;
    public static boolean playerIsWhite = false;
    public static boolean isWallDrop = false;


    public void initialize() {

        //display white player's pawn
        Circle whitePawn = new Circle();
//       awn.setCenterX(0);
//      pawn.setCenterY(0);
        whitePawn.setLayoutX(189);
        whitePawn.setLayoutY(17);
        whitePawn.setRadius(8);
        whitePawn.setFill(Color.web("#1e90ff"));
        board.getChildren().add(whitePawn);

        Circle blackPawn = new Circle();
//       awn.setCenterX(0);
//      pawn.setCenterY(0);
        blackPawn.setLayoutX(189);
        blackPawn.setLayoutY(363);
        blackPawn.setRadius(8);
        blackPawn.setFill(Color.web("#5aff1e"));
        board.getChildren().add(blackPawn);



        Circle pawn = new Circle();
//       awn.setCenterX(0);
//      pawn.setCenterY(0);
        pawn.setLayoutX(189);
        pawn.setLayoutX(17);
        pawn.setRadius(8);
        pawn.setFill(Color.web("#1e90ff"));
        board.getChildren().add(pawn);


        //display player name
        whitePlayerName.setText(ModelQuery.getWhitePlayer().getUser().getName());
        blackPlayerName.setText(ModelQuery.getBlackPlayer().getUser().getName());


        //display player name on the thinking time section
        whitePlayerName1.setText(ModelQuery.getWhitePlayer().getUser().getName());
        blackPlayerName1.setText(ModelQuery.getBlackPlayer().getUser().getName());

        //start the clock once the game is initiated
        StartNewGameController.startTheClock();
        timerForWhitePlayer.setText(StartNewGameController.toTimeStr());
        timerForBlackPlayer.setText(StartNewGameController.toTimeStr());

        if (timeline != null) {
            timeline.stop();
        }
        // update timerLabel
        //timerForWhitePlayer.setText(StartNewGameController.toTimeStr());
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        EventHandler onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Player currentPlayer = ModelQuery.getPlayerToMove();
                boolean zeroTimeLeft = currentPlayer.getRemainingTime().getTime() <= 0;
                if ((zeroTimeLeft == true) || (isWallDrop == true) ) {
                    /*
                     *
                     * TODO: switch Player
                     * Player nextPlayer = currentPlayer.getNextPlayer();
                     * SwitchPlayerController.SwitchActivePlayer(nextPlayer);
                     * TODO: count down time for the next player
                     */
                    // currentPlayer.setNextPlayer(currentPlayer.getNextPlayer());

                }
                else {
                    String nextPlayer = ModelQuery.getPlayerToMove().getNextPlayer().getUser().getName();

                    //grey out the next player name & count down time for the current player
                    if (nextPlayer.equals(blackPlayerName.getText())) {
                        blackPlayerName.setFill(Color.LIGHTGRAY);
                        timerForWhitePlayer.setText(StartNewGameController.toTimeStr());
                    } else {
                        whitePlayerName.setFill(Color.LIGHTGRAY);
                        timerForBlackPlayer.setText(StartNewGameController.toTimeStr());
                    }

                }
            }
        };
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), onFinished));
        timeline.playFromStart();
    }

    public void handleBackToMenu(ActionEvent actionEvent) {
            timeline.stop();
            changePage("/fxml/Menu.fxml");

    }

    public void createNewWall(ActionEvent actionEvent) {
        try {
            wall = new Rectangle(206, 86, 9, 77);
            wall.setArcWidth(5);
            wall.setArcHeight(5);
            wall.setFill(Color.web("#5aff1e"));
            wall.setStroke(Color.web("#000000"));
            wall.setStrokeWidth(1.5);
            wall.setStrokeType(StrokeType.INSIDE);
            board.getChildren().add(wall);
        } catch (Exception e){
            System.out.println("Error");
        }
    }

      public void handleKeyPressed(KeyEvent keyEvent) {


//
//        //Moves the wall up
//        if(keyEvent.equals(VK_W)){
//            wall.setY();
//        }
//        //Moves the wall left
//        else if(keyEvent.equals(VK_A)){
//            wall.setX();
//        }
//        //Moves the wall down
//        else if(keyEvent.equals(VK_S)){
//            wall.setY();
//        }
//        //Moves the wall right
//        else if(keyEvent.equals(VK_D)){
//            wall.setX();
//        }
//        //Confirm wall placement and drops the wall
//        else if(keyEvent.equals(VK_SPACE)){
//
//        }

          // TODO SET NUMBER OF WALLS -1 FOR THE CURRENT PLAYER
          // TODO CHECK NUMBER OF WALLS , IF ITS ZERO, PLAYER SHOULD BE NOTFIED "I shall be notified that I have no more walls"
          // TODO SAVE GAME POSITION WHEN A MOVE IS DONE
    }

    public void handleClearBoard(ActionEvent actionEvent) {
        board.getChildren().clear();
    }
}
