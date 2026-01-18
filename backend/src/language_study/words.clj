(ns language_study.words
  (:require
    [language_study.database :as db]
    [clojure.string :as str]
    [language_study.ai :as ai]
    [dk.ative.docjure.spreadsheet :as excel]
    [language_study.validators :as validator])
  (:import (java.time LocalDate)
           (java.time.temporal ChronoUnit)))

(defn add-word [user-id word translation cat-id]
  (try
    (db/add-word! user-id word translation cat-id)
    (println "Word added successfully!")
    (catch Exception e
      (println "Error adding word:" (.getMessage e)))))

(defn get-words [user-id]
  (db/get-words user-id))

(defn categories-of-user [user-id]
  (db/categories-of-user user-id))

(defn success_rate [row]
  (if (zero? (:total_count row))
    0
    (/ (:correct_answers row)
       (:total_count row))))

(defn statistics []
  (sort-by :success >
           (map (fn [row]
                  {:word        (:word row)
                   :translation (:translation row)
                   :success     (success_rate row)})
                (db/load-all-words))))

(defn compare-words [word1 word2]
  (= (.toLowerCase word1) (.toLowerCase word2)))

(defn read-from-file [file-name]
  (map #(str/split % #" ") (str/split-lines (slurp file-name))))

(defn weight-of-word [row]
  (let [correct (:correct_answers row)
        total   (:total_count row)
        last-attend (:last_attend row)
        today (LocalDate/now)
        days-since (.until last-attend today ChronoUnit/DAYS)
        base-weight (if (zero? total)
                        1.0
                        (- 1 (/ correct total)))]
      (+ base-weight days-since)))

(defn weighted-rand [items weight-fn]
  (loop [r (* (reduce + (map weight-fn items)) (rand))
         items items]
    (if (<= r (weight-fn (first items)))
      (first items)
      (recur (- r (weight-fn (first items))) (rest items)))))

(defn get-random-word [user-id]
  (let [rows (db/get-words user-id)]
    (weighted-rand rows weight-of-word)))

(def default-path "reports/export.xlsx")

(defn export-xlsx! [user-id]
  (let [words (db/get-words user-id)
        wb (excel/create-workbook
             "Words"
             (concat
               [["word" "translation" "success" "total"]]
               words))]
    (excel/save-workbook! default-path wb)
    (println "Export finished. Saved as:" default-path)))

(defn multiple-choice-exercise [user]
  (let [row (get-random-word (:id user))
        correct (:translation row)
        word (:word row)
        id (:id row)
        other-words (->> (db/get-words (:id user))
                         (remove #(= (:translation %) correct))
                         (map :translation)
                         shuffle
                         (take 4))
        options (shuffle (conj other-words correct))
        num-of-options (count options)]

    (println "\nTranslation:" word)
    (doseq [[i opt] (map-indexed vector options)]
      (println (inc i) ")" opt))

    (print "Choose the correct word: ") (flush)
    (let [user-choice (Integer/parseInt (read-line))]
      (if (validator/valid-option num-of-options user-choice)
        (if (compare-words (nth options (dec user-choice)) correct)
               (do
                 (println "Correct!")
                 (db/update-word-stats id true))
               (do
                 (println "Wrong! Correct answer is:" correct)
                 (db/update-word-stats id false)))
        (println "Your choice doesn't exist.")))))

(defn translate-word-exercise [user]
  (let [row (get-random-word (:id user))]
    (when row
      (println "Word:" (:word row))
      (print "Your translation: ") (flush)
      (let [user-answer (read-line)]
        (if (compare-words user-answer (:translation row))
          (do
            (println "Correct!")
            (db/update-word-stats (:id row) true))
          (do
            (println "Wrong! Correct translation is:" (:translation row))
            (db/update-word-stats (:id row) false)))))))

(defn translate-sentence-exercise [id]
  (let [words (vec (db/list-words-for-ai id))
        sentence (ai/generate-sentence words)]
    (when sentence
      (println words)
      (println "Sentence:" sentence)
      (print "Your translation: ") (flush)
      (let [user-input (read-line)
            feedback (ai/check-translation sentence user-input)]
        (println feedback))
      )))
