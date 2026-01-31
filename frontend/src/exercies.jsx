import React, { useState } from "react";
import Button from "./components/button";

export default function Exercises() {
  const [type, setType] = useState(null);

  return (
    <div>
      <h1>Exercises</h1>

      <div className="exercise-buttons">
        <Button
          text="Translate word"
          onClick={() => setType("translate")}
        ></Button>
        <Button
          text="Multiple choice"
          onClick={() => setType("choice")}
        ></Button>
        <Button
          text="Translate sentence"
          onClick={() => setType("sentence")}
        ></Button>
      </div>

      <div className="exercise-content">
        {type === "translate" && <TranslateWord />}
        {type === "choice" && <MultipleChoice />}
        {type === "sentence" && <TranslateSentence />}
      </div>
    </div>
  );
}
