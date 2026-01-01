const API_BASE_URL = "http://localhost:8081/api/crossword";

export const api = {
  // New proper crossword puzzle endpoint
  getPuzzle: async () => {
    const response = await fetch(`${API_BASE_URL}/puzzle`);
    return response.json();
  },

  generateNewPuzzle: async () => {
    const response = await fetch(`${API_BASE_URL}/puzzle/new`);
    return response.json();
  },

  // Legacy endpoints (keeping for backwards compatibility)
  getRandomCrossword: async () => {
    const response = await fetch(`${API_BASE_URL}/random`);
    return response.text();
  },

  getRandomCrosswordWithWords: async () => {
    const response = await fetch(`${API_BASE_URL}/random/details`);
    return response.json();
  },

  getRandomCrosswordWithClues: async () => {
    const response = await fetch(`${API_BASE_URL}/random/clues`);
    return response.json();
  },

  subscribeToSolverSteps: (onUpdate) => {
    console.log("[SSE] Connecting to solver steps...");
    const eventSource = new EventSource(`${API_BASE_URL}/solve/steps`);

    eventSource.addEventListener("solver-update", (event) => {
      try {
        console.log("[SSE] Received update:", event.data);
        const data = JSON.parse(event.data);

        // Handle grid conversion safely
        let parsedGrid = data.grid;
        if (Array.isArray(parsedGrid)) {
          if (typeof parsedGrid[0] === "string") {
            parsedGrid = parsedGrid.map((row) =>
              row.split("").map((cell) => (cell === "-" ? "" : cell))
            );
          } else if (Array.isArray(parsedGrid[0])) {
            parsedGrid = parsedGrid.map((row) =>
              row.map((cell) => (cell === "-" ? "" : cell))
            );
          }
        }

        onUpdate({
          ...data,
          grid: parsedGrid,
        });

        if (data.action === "solved") {
          console.log("[SSE] Puzzle solved, closing connection");
          eventSource.close();
        }
      } catch (error) {
        console.error("[SSE] Error parsing event data:", error);
      }
    });

    eventSource.onerror = (error) => {
      console.error("[SSE] Error:", error);
      if (eventSource.readyState === EventSource.CLOSED) {
        console.log("[SSE] Connection was closed");
      }
      eventSource.close();
    };

    return () => {
      console.log("[SSE] Closing connection");
      eventSource.close();
    };
  },
};
