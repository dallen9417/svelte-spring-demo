import type {PageLoad} from "./$types";

export const load = (async ({ fetch }) => {
  const response = await fetch("/api/start");
  const game = await response.json();

  return {
    /**
     * The player's guessed words so far
     */
    guesses: game.guesses,

    /**
     * An array of strings like '__x_c' corresponding to the guesses, where 'x' means
     * an exact match, and 'c' means a close match (right letter, wrong place)
     */
    answers: game.answers,

    /**
     * The correct answer, revealed if the game is over
     */
    answer: game.answers.length >= 6 ? game.answer : null
  };
}) satisfies PageLoad;
