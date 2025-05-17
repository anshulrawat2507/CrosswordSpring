export const mockCrosswordData = {
  grid: [
    ["A", "B", "C", "", "D", "E", "F"],
    ["G", "", "H", "I", "", "J", ""],
    ["K", "L", "", "M", "N", "", "O"],
    ["", "P", "Q", "", "R", "S", "T"],
    ["U", "", "V", "W", "", "X", ""],
    ["Y", "Z", "", "A", "B", "", "C"],
    ["", "D", "E", "F", "", "G", "H"]
  ],
  // This represents the correct solution
  solution: [
    ["A", "B", "C", "", "D", "E", "F"],
    ["G", "", "H", "I", "", "J", ""],
    ["K", "L", "", "M", "N", "", "O"],
    ["", "P", "Q", "", "R", "S", "T"],
    ["U", "", "V", "W", "", "X", ""],
    ["Y", "Z", "", "A", "B", "", "C"],
    ["", "D", "E", "F", "", "G", "H"]
  ],
  clues: {
    across: [
      { number: 1, clue: "Beginning of the alphabet", answer: "ABC" },
      { number: 4, clue: "Common abbreviation for 'Digital Experience Framework'", answer: "DEF" },
      { number: 7, clue: "Abbreviation for the seventh letter", answer: "G" },
      { number: 8, clue: "Greeting exclamation", answer: "HI" },
      { number: 9, clue: "Tenth letter", answer: "J" },
      { number: 10, clue: "Abbreviation for 'Okay'", answer: "K" },
      { number: 11, clue: "Letter after K", answer: "L" },
      { number: 12, clue: "Middle letters of the alphabet", answer: "MN" },
      { number: 13, clue: "Letter after N", answer: "O" },
      { number: 14, clue: "Letters P, Q", answer: "PQ" },
      { number: 15, clue: "Common abbreviation for 'Registered Trademark'", answer: "RST" }
    ],
    down: [
      { number: 1, clue: "First letter", answer: "A" },
      { number: 2, clue: "Second letter", answer: "B" },
      { number: 3, clue: "Third letter", answer: "C" },
      { number: 4, clue: "Fourth letter", answer: "D" },
      { number: 5, clue: "Fifth letter", answer: "E" },
      { number: 6, clue: "Sixth letter", answer: "F" },
      { number: 7, clue: "Seventh letter", answer: "G" }
    ]
  }
};
