import React, { useState } from "react";
import Button from "./components/button";
import TranslateWord from "./components/translateWord";
import MultipleChoice from "./components/multipleChoice";
import TranslateSentence from "./components/translateSentence";

export default function Exercises({ userId }) {
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
        {type === "translate" && <TranslateWord userId={userId} />}
        {type === "choice" && <MultipleChoice userId={userId} />}
        {type === "sentence" && <TranslateSentence userId={userId} />}
      </div>
    </div>
  );
}
