(ns language_study.words
  (:require
    [language_study.database :as db]
    [clojure.string :as str]
    [language_study.ai :as ai]
    [dk.ative.docjure.spreadsheet :as excel])
  (:import (java.time LocalDate)
           (java.time.temporal ChronoUnit)))

(defn add-word [user-id word translation cat-id]
  (try
    (db/add-word! user-id word translation cat-id)
    (println "Word added successfully!")
    (catch Exception e
      (println "Error adding word:" (.getMessage e)))))

(defn delete-word! [word-id]
  (db/delete-word! word-id))

(defn success_rate [row]
  (if (zero? (:total_count row))
    0
    (/ (:correct_answers row)
       (:total_count row))))

(defn get-words [user-id]
  (map (fn [row]
         {:id          (:id row)
          :word        (:word row)
          :translation (:translation row)
          :cat-id      (:cat_id row)
          :success     (int (* 100 (success_rate row)))})
       (db/get-words user-id)))

(defn categories-of-user [user-id]
  (db/categories-of-user user-id))

(defn add-category [user-id name]
  (db/add-category! user-id name))

(defn statistics [user-id]
  (sort-by :success >
           (map (fn [row]
                  {:word        (:word row)
                   :translation (:translation row)
                   :success     (success_rate row)})
                (db/get-words user-id))))

(defn compare-words [word1 word2]
  (= (.toLowerCase word1) (.toLowerCase word2)))

(defn read-from-file [file-name]
  (map #(str/split % #" ") (str/split-lines (slurp file-name))))

(defn weight-of-word [row]
  (let [correct (:correct_answers row)
        total   (:total_count row)
        last-attend (-> (:last_attend row)
                        (.toLocalDate))
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

(defn map-words-for-xlsx
  [words]
  (map (fn [m]
         [(:word m)
          (:translation m)
          (:correct_answers m)
          (:total_count m)])
       words))

(defn export-xlsx! [user-id file-name]
  (let [words (db/get-words user-id)
        wb (excel/create-workbook
             "Words"
             (concat
               [["word" "translation" "success" "total"]]
               (map-words-for-xlsx words)))]
    (excel/save-workbook! (str "report/" (str file-name ".xlsx")) wb)))

(defn generate-multiple-choice [user-id]
  (let [row (get-random-word user-id)
        correct (:translation row)
        other-words (->> (db/get-words user-id)
                         (remove #(= (:translation %) correct))
                         (map :translation)
                         shuffle
                         (take 4))
        options (shuffle (conj other-words correct))]
    {:word (:word row)
     :options options
     :word-id (:id row)}))

(defn get-translate-word [user-id]
  (when-let [row (get-random-word user-id)]
    (println row)
    {:word-id (:id row)
     :word (:word row)}))

(defn check-translate-word [word-id user-answer]
  (let [row (db/get-word-by-id word-id)
        correct (:translation row)
        correct? (compare-words user-answer correct)]
    (db/update-word-stats word-id correct?)
    {:correct correct?
     :correct-answer correct}))

(defn generate-sentence-exercise [user-id]
  (let [words (vec (db/list-words-for-ai user-id))
        sentence (ai/generate-sentence words)]
    (when sentence
      {:words words
       :sentence sentence})))

(defn check-translation-exercise [sentence user-input]
  (ai/check-translation sentence user-input))
