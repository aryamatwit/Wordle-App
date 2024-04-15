package wordle;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// Abstract class for word generator
abstract class WordGenerator<T> {
	public abstract T getRandomWord();
}

// Implementation of word generator for string words
class StringWordGenerator extends WordGenerator<String> {
	private final List<String[]> categories;

	public StringWordGenerator() {
		categories = new ArrayList<>();
		categories.add(new String[] { "ford", "chevrolet", "bmw", "volkswagen", "honda", "nissan", "acura", "dodge",
				"audi", "mercedes", "amg" });
		categories.add(new String[] { "pizza", "tacos", "burrito", "pupusas", "chicken-parm", "salad" });
		categories.add(new String[] { "Lynn", "Boston", "Roxbury", "Revere", "Somerville", "Cambridge", "Saugus", "Salem" });
	}

	
	public void getWordsFromFile(String file) {
		// file path
		// loop
		// append to array 
		// return array
	}
	
	@Override
	public String getRandomWord() {
		Random random = new Random();
		int categoryIndex = random.nextInt(categories.size());
		String[] category = categories.get(categoryIndex);
		return category[random.nextInt(category.length)];
	}
}

// Abstract class for game
abstract class Game<T> {
	protected T secretWord;
	protected List<Character> guessedCharacters;

	public Game(T secretWord) {
		this.secretWord = secretWord;
		this.guessedCharacters = new ArrayList<>();
	}

	public abstract String getMaskedWord();

	public abstract boolean guessCharacter(char c);

	public abstract boolean isGameOver();
}

// Implementation of game for Wordle
class WordleGame extends Game<String> {
	private static final int MAX_TRIES = 6;
	int remainingTries;

	public WordleGame(String secretWord) {
		super(secretWord);
		this.remainingTries = MAX_TRIES;
	}

	@Override
	public String getMaskedWord() {
		StringBuilder masked = new StringBuilder();
		for (char c : secretWord.toCharArray()) {
			if (guessedCharacters.contains(c)) {
				masked.append(c);
			} else {
				masked.append("_");
			}
			masked.append(" ");
		}
		return masked.toString();
	}

	@Override
	public boolean guessCharacter(char c) {
		guessedCharacters.add(c);
		if (!secretWord.contains(String.valueOf(c))) {
			remainingTries--;
			return false;
		}
		return true;
	}

	@Override
	public boolean isGameOver() {
		return remainingTries <= 0 || getMaskedWord().indexOf('_') == -1;
	}
}

public class WordleApp extends Application {
	private WordleGame wordleGame;
	private Pane wordlePane;

	@Override
	public void start(Stage primaryStage) {
		WordGenerator<String> wordGenerator = new StringWordGenerator();
		String secretWord = wordGenerator.getRandomWord().toLowerCase();
		wordleGame = new WordleGame(secretWord);

		VBox root = new VBox();
		root.setBackground(new Background(new BackgroundFill(Color.MAROON, CornerRadii.EMPTY, Insets.EMPTY)));

		HBox hangmanBox = new HBox();
		hangmanBox.setAlignment(Pos.CENTER);
		wordlePane = createWordlePane();
		hangmanBox.getChildren().add(wordlePane);

		Label wordLabel = new Label(wordleGame.getMaskedWord());
		wordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		wordLabel.setTextFill(Color.WHITE);
		Label remainingTriesLabel = new Label("Remaining Tries: " + wordleGame.remainingTries);
		remainingTriesLabel.setTextFill(Color.WHITE);
		TextField guessInput = new TextField();
		Button guessButton = new Button("Guess");

		guessButton.setOnAction(event -> {
			String guess = guessInput.getText().toLowerCase();
			if (guess.length() == 1 && Character.isLetter(guess.charAt(0))) {
				char guessedChar = guess.charAt(0);
				if (!wordleGame.guessedCharacters.contains(guessedChar)) {
					boolean correctGuess = wordleGame.guessCharacter(guessedChar);
					wordLabel.setText(wordleGame.getMaskedWord());
					if (!correctGuess) {
						remainingTriesLabel.setText("Remaining Tries: " + wordleGame.remainingTries);
						updateWordle();
					}
				if (wordleGame.isGameOver()) {
						endGame(primaryStage, wordleGame.secretWord, wordleGame.remainingTries > 0);
					}
				}
			}
			guessInput.clear();
		});

		HBox titleBox = new HBox();
		titleBox.setAlignment(Pos.CENTER);
		titleBox.getChildren().add(new Label("WORDLE"));
		titleBox.setStyle("-fx-font-size: 30; -fx-text-fill: black;");

		VBox labelsBox = new VBox(10);
		labelsBox.setAlignment(Pos.CENTER);
		labelsBox.getChildren().addAll(titleBox, wordLabel, remainingTriesLabel);

		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.add(labelsBox, 0, 0);
		gridPane.add(guessInput, 0, 1);
		gridPane.add(guessButton, 1, 1);

		VBox vbox = new VBox(10);
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(hangmanBox, gridPane);

		root.getChildren().add(vbox);

		Scene scene = new Scene(root, 400, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Wordle");
		primaryStage.show();
	}

	private Pane createWordlePane() {
		Pane pane = new Pane();
		pane.setPrefSize(200, 200);

		Line scaffold = new Line(20, 190, 80, 190);
		scaffold.setStroke(Color.BLACK);
		scaffold.setStrokeWidth(5);
		pane.getChildren().add(scaffold);

		Line upright = new Line(50, 190, 50, 50);
		upright.setStroke(Color.BLACK);
		upright.setStrokeWidth(5);
		pane.getChildren().add(upright);

		Line beam = new Line(50, 50, 150, 50);
		beam.setStroke(Color.BLACK);
		beam.setStrokeWidth(5);
		pane.getChildren().add(beam);

		Line rope = new Line(150, 50, 150, 80);
		rope.setStroke(Color.BLACK);
		rope.setStrokeWidth(5);
		pane.getChildren().add(rope);

		Circle head = new Circle(150, 100, 20);
		head.setStroke(Color.BLACK);
		head.setStrokeWidth(5);
		head.setFill(null);
		pane.getChildren().add(head);

		Line body = new Line(150, 120, 150, 150);
		body.setStroke(Color.BLACK);
		body.setStrokeWidth(5);
		pane.getChildren().add(body);

		Line leftArm = new Line(150, 130, 130, 140);
		leftArm.setStroke(Color.BLACK);
		leftArm.setStrokeWidth(5);
		pane.getChildren().add(leftArm);

		Line rightArm = new Line(150, 130, 170, 140);
		rightArm.setStroke(Color.BLACK);
		rightArm.setStrokeWidth(5);
		pane.getChildren().add(rightArm);

		Line leftLeg = new Line(150, 150, 135, 170);
		leftLeg.setStroke(Color.BLACK);
		leftLeg.setStrokeWidth(5);
		pane.getChildren().add(leftLeg);

		Line rightLeg = new Line(150, 150, 165, 170);
		rightLeg.setStroke(Color.BLACK);
		rightLeg.setStrokeWidth(5);
		pane.getChildren().add(rightLeg);

		return pane;
	}

	private void updateWordle() {
		int remainingTries = wordleGame.remainingTries;
		if (remainingTries >= 0) {
			if (remainingTries + 5 < wordlePane.getChildren().size()) {
				((Line) wordlePane.getChildren().get(remainingTries + 5)).setVisible(true);
			}
		}
		if (remainingTries <= -2 && !((Circle) wordlePane.getChildren().get(4)).isVisible()) {
			((Circle) wordlePane.getChildren().get(4)).setVisible(true);
		}
		if (remainingTries <= -3) {
			((Line) wordlePane.getChildren().get(9)).setVisible(true);
		}
		if (remainingTries <= -4) {
			((Line) wordlePane.getChildren().get(10)).setVisible(true);
		}
		if (remainingTries <= -5) {
			((Line) wordlePane.getChildren().get(11)).setVisible(true);
		}
		if (remainingTries <= -6) {
			((Line) wordlePane.getChildren().get(12)).setVisible(true);
		}
	}

	private void endGame(Stage primaryStage, String secretWord, boolean won) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Game Over");
		if (won) {
			alert.setHeaderText("Congratulations! You've won!");
		} else {
			alert.setHeaderText("Game Over! The word was: " + secretWord);
		}
		alert.setContentText("Thanks for playing Wordle!");
		alert.setOnCloseRequest(event -> primaryStage.close());
		alert.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

