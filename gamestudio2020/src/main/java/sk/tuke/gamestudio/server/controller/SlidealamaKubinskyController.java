package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.Field;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.GameState;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.Player;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.PlayerNames;
import sk.tuke.gamestudio.game.slidealama.kubinsky.core.tiles.Tile;
import sk.tuke.gamestudio.service.*;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping("/slidealama-kubinsky")
public class SlidealamaKubinskyController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserController userController;

    private Field field;

    private Tile thisTurnTile;
    private Tile nextTurnTile;

    private boolean scoreSaved = false;
    private boolean namesSet = false;

    public static final String GAME_NAME = "SLIDE-A-LAMA";
    public static final String TEMPLATE_NAME = "slidealama-kubinsky";

    @RequestMapping
    public String slidealama(@RequestParam(value = "position", required = false) String position,
                        Model model) throws CommentException, RatingException {

        if (field == null) {
            newGame();
        }

        if (position != null && !isGameWon()) {
            play(position);
        }

        if (isGameWon() && !scoreSaved) {
            saveScore();
        }

        prepareModel(model);

        return TEMPLATE_NAME;
    }

    @RequestMapping("/new")
    public String newGame(Model model) throws CommentException, RatingException {
        newGame();
        prepareModel(model);
        return TEMPLATE_NAME;
    }

    @RequestMapping(value = "/processComment", method = RequestMethod.POST)
    public String processComment(@ModelAttribute(value = "comment") Comment comment,
                                 @RequestParam(value = "action") String playerNumber,
                                 Model model) throws CommentException, RatingException {

        String playerName;

        switch (Integer.parseInt(playerNumber)) {
            case 1:
                playerName = field.getPlayerOne().getName();
                break;
            case 2:
                playerName = field.getPlayerTwo().getName();
                break;
            default:
                return TEMPLATE_NAME;
        }

        comment.setPlayer(playerName);
        comment.setCommentedOn(new java.util.Date());
        comment.setGame(GAME_NAME);
        commentService.addComment(comment);

        prepareModel(model);

        return TEMPLATE_NAME;
    }

    @RequestMapping(value = "/processRating", method = RequestMethod.POST)
    public String processRating(@ModelAttribute(value = "rating") Rating rating,
                                @RequestParam(value = "action") String playerNumber,
                                Model model) throws CommentException, RatingException {

        String playerName;

        switch (Integer.parseInt(playerNumber)) {
            case 1:
                playerName = field.getPlayerOne().getName();
                break;
            case 2:
                playerName = field.getPlayerTwo().getName();
                break;
            default:
                return TEMPLATE_NAME;
        }

        rating.setPlayer(playerName);
        rating.setRatedon(new java.util.Date());
        rating.setGame(GAME_NAME);
        ratingService.setRating(rating);

        prepareModel(model);

        return TEMPLATE_NAME;
    }

    @RequestMapping(value = "/processNames", method = RequestMethod.POST)
    public String processNames(@ModelAttribute(value = "playerNames") PlayerNames playerNames,
                                Model model) throws CommentException, RatingException {

        field.getPlayerOne().setName(playerNames.getPlayerOneName());
        field.getPlayerTwo().setName(playerNames.getPlayerTwoName());
        namesSet = true;

        prepareModel(model);

        return TEMPLATE_NAME;
    }

    private void prepareModel(Model model) throws CommentException, RatingException {
        model.addAttribute("playerOneName", getPlayerOneName());
        model.addAttribute("playerTwoName", getPlayerTwoName());

        model.addAttribute("playersHtml", getHtmlPlayers());
        model.addAttribute("fieldHtml", getHtmlField());

        model.addAttribute("gameState", field.getGameState());
        model.addAttribute("namesSet", namesSet);

        model.addAttribute("scores", scoreService.getTopScores(GAME_NAME));
        model.addAttribute("score", new Score());

        model.addAttribute("comments", commentService.getComments(GAME_NAME));
        model.addAttribute("comment", new Comment());

        model.addAttribute("playerOneRating", getRating(getPlayerOneName()));
        model.addAttribute("playerTwoRating", getRating(getPlayerTwoName()));
        model.addAttribute("avgRating", ratingService.getAverageRating(GAME_NAME));
        model.addAttribute("rating", new Rating());

        model.addAttribute("playerNames", new PlayerNames());
    }

    private void newGame() {
        field = new Field("Player 1", "Player 2");

        thisTurnTile = field.getRandomTile();
        nextTurnTile = field.getRandomTile();

        scoreSaved = false;
        namesSet = false;
    }

    private void play(String position) {
        if (!field.insertTile(position, thisTurnTile)) {
            return;
        }

        // check same kinds and gravity
        do field.checkGravity();
        while (field.checkAdjacentTiles());

        // check end game
        field.checkIfSolved();
        field.incrementRound();

        // switch tiles and get new
        thisTurnTile = nextTurnTile;
        nextTurnTile = field.getRandomTile();
    }

    private boolean isGameWon() {
        if (field.getGameState() == GameState.SOLVED) {
            return true;
        } else {
            return false;
        }
    }

    private void saveScore() {
        Player player1 = field.getPlayerOne();
        Player player2 = field.getPlayerTwo();
        Player winner;

        if (player1.getScore() > player2.getScore()) {
            winner = player1;
        } else {
            winner = player2;
        }

        Score score = new Score(winner.getName(), winner.getScore(), GAME_NAME, new java.util.Date());
        scoreService.addScore(score);

        scoreSaved = true;
    }

    private String getPlayerOneName() {
        return field.getPlayerOne().getName();
    }

    private String getPlayerTwoName() {
        return field.getPlayerTwo().getName();
    }

    private String getRating(String playerName) throws RatingException {
        int rating = ratingService.getRating(GAME_NAME, playerName);

        if (rating == -1) {
            return "Not yet rated";
        } else {
            return String.valueOf(rating);
        }
    }

    private String getHtmlPlayers() {
        StringBuilder sb = new StringBuilder();
        Player[] players = {field.getPlayerOne(), field.getPlayerTwo()};

        for (int playerNumber = 0; playerNumber < 2; playerNumber++) {
            boolean[] isPlayerWinner = {(players[0].getScore() >= 300), (players[1].getScore() >= 300)};
            boolean isPlayersRound = (field.getRoundCount() % 2 == playerNumber);
            int otherPlayerNumber = Math.abs(playerNumber - 1);

            sb.append("<div class='col-xs-12 col-sm-12 col-md-12 col-lg-4 col-xl-4'>");
            sb.append("<div class='p-3 rounded mb-3");

            if (isPlayerWinner[playerNumber]) {
                sb.append(" bg-yellow-pulse'>\n");
            } else if (isPlayersRound && !isPlayerWinner[otherPlayerNumber]) {
                sb.append(" bg-grey-pulse'>\n");
            } else {
                sb.append(" bg-light'>\n");
            }

            sb.append("<div class='row'>\n");
            sb.append("<div class='col-8'>\n");
            sb.append("<h3 class='font-weight-bold'>" + players[playerNumber].getName() + "</h3>\n");
            sb.append("<span>");

            if (isPlayerWinner[playerNumber]) {
                sb.append("Won with ");
            }

            sb.append(players[playerNumber].getScore() + " points</span>\n");
            sb.append("</div>\n");

            sb.append("<div class='col-4' align='right'>\n");
            sb.append("<img src='/images/slidealama/kubinsky/");

            if (isPlayersRound) {
                sb.append(thisTurnTile.getImageName());
            } else {
                sb.append(nextTurnTile.getImageName());
            }

            sb.append(".png'>\n");
            sb.append("</div>\n");
            sb.append("</div>\n");
            sb.append("</div>\n");
            sb.append("</div>\n");
        }

        return sb.toString();
    }

    private String getHtmlField() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>\n");

        // up buttons
        sb.append("<tr>\n");
        for (int column = 0; column < 7; column++) {
            sb.append("<td class='td-center pb-3'>\n");

            if (column != 0 && column != 6) {
                sb.append("<a href='/slidealama-kubinsky?position=" +
                        String.format("%c", 69 + column) +
                        "' class='btn btn-primary");

                if (isGameWon()) {
                    sb.append(" disabled");
                }

                sb.append("'><i class='fas fa-chevron-down'></i></a>\n");
            }

            sb.append("</td>\n");
        }
        sb.append("</tr>\n");

        for (int row = 0; row < 5; row++) {
            // left buttons
            sb.append("<tr>\n");
            sb.append("<td class='td-center pr-3'>\n");
            sb.append("<a href='/slidealama-kubinsky?position=" +
                    String.format("%c", 69 - row) +
                    "' class='btn btn-primary");

            if (isGameWon()) {
                sb.append(" disabled");
            }

            sb.append("'><i class='fas fa-chevron-right'></i></a>\n");
            sb.append("</td>\n");

            // images
            for (int column = 0; column < 5; column++) {
                Tile tile = field.getTile(row, column);
                sb.append("<td class='td-center'>\n");
                sb.append("<img src='/images/slidealama/kubinsky/" + tile.getImageName() + ".png'>\n");
                sb.append("</td>\n");
            }

            // right buttons
            sb.append("<td class='td-center pl-3'>\n");
            sb.append("<a href='/slidealama-kubinsky?position=" +
                    String.format("%c", 75 + row) +
                    "' class='btn btn-primary");

            if (isGameWon()) {
                sb.append(" disabled");
            }

            sb.append("'><i class='fas fa-chevron-left'></i></a>\n");

            sb.append("</td>\n");

            sb.append("</tr>\n");
        }

        sb.append("</table>\n");
        return sb.toString();
    }

}
