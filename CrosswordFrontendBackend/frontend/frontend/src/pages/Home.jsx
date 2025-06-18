import React from "react";
import { Link } from "react-router-dom";

const Home = () => {
  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-indigo-100 flex flex-col items-center justify-center p-6 text-center">
      <div className="max-w-3xl mx-auto animate-fade-in">
        <h1 className="text-5xl md:text-6xl font-bold text-blue-700 mb-6 drop-shadow-lg animate-zoom-in">
          The Ultimate Crossword Challenge
        </h1>

        <p
          className="text-lg md:text-xl text-gray-700 mb-10 leading-relaxed animate-fade-in opacity-0"
          style={{ animationDelay: "300ms", animationFillMode: "forwards" }}
        >
          Dive into a world where words come alive! Each puzzle is a fresh
          adventure—designed to tease your brain, sharpen your wit, and bring a
          smile to your day.
        </p>

        <div className="relative w-full max-w-md mx-auto h-2 mb-10">
          <div className="absolute inset-0 bg-gradient-to-r from-blue-200 via-blue-400 to-indigo-500 rounded-full animate-pulse-light"></div>
        </div>

        <p
          className="text-lg md:text-xl text-gray-700 mb-12 leading-relaxed animate-fade-in opacity-0"
          style={{ animationDelay: "600ms", animationFillMode: "forwards" }}
        >
          Whether you're a puzzle pro or just looking for a fun break, our
          crosswords are crafted to delight, surprise, and inspire. Take a
          moment, grab your favorite beverage, and see how many clues you can
          crack!
        </p>

        <div
          className="flex flex-col sm:flex-row gap-4 justify-center mb-12 animate-fade-in opacity-0"
          style={{ animationDelay: "900ms", animationFillMode: "forwards" }}
        >
          <Link
            to="/play"
            className="px-8 py-4 bg-blue-600 text-white rounded-lg font-semibold shadow-lg hover:bg-blue-700 hover:scale-105 hover:shadow-xl transition-all duration-300 text-lg"
          >
            Start Playing
          </Link>
          <Link
            to="/how-to-play"
            className="px-8 py-4 bg-white border-2 border-blue-600 text-blue-600 rounded-lg font-semibold shadow-lg hover:bg-blue-50 hover:scale-105 hover:shadow-xl transition-all duration-300 text-lg"
          >
            How to Play
          </Link>
        </div>

        <div
          className="mt-8 text-center animate-fade-in opacity-0"
          style={{ animationDelay: "1200ms", animationFillMode: "forwards" }}
        >
          <p className="text-blue-600 font-medium text-lg mb-2">
            "A puzzle a day keeps the boredom away!"
          </p>
          <p className="text-gray-500 italic">
            Join our growing community of word enthusiasts and puzzle solvers.
          </p>
        </div>
      </div>

      {/* Decorative elements */}
      <div className="absolute top-20 left-10 w-16 h-16 border-4 border-blue-200 rounded-lg opacity-30 animate-spin-slow hidden md:block"></div>
      <div className="absolute bottom-20 right-10 w-20 h-20 border-4 border-indigo-200 rounded-full opacity-30 animate-bounce-slow hidden md:block"></div>
      <div className="absolute top-40 right-20 w-10 h-10 bg-blue-300 rounded-full opacity-20 animate-pulse hidden md:block"></div>
      <div className="absolute bottom-40 left-20 w-12 h-12 bg-indigo-300 rounded-full opacity-20 animate-pulse hidden md:block"></div>
    </div>
  );
};

export default Home;
