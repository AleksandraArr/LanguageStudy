(ns language_study.words
  (:require
    [language_study.database :as db]
    [language_study.ai :as ai]
    [clojure.string :as str]
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

(defn edit-word!
  [word-id word translation cat-id]
  (try
    (db/update-word! word-id word translation cat-id)
    (println "Word updated successfully!")
    (catch Exception e
      (println "Error updating word:" (.getMessage e)))))

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
          :category_id      (:category_id row)
          :success     (int (* 100 (success_rate row)))
          :category_name (:category_name row)})
       (db/get-words user-id)))

(defn categories-of-user [user-id]
  (db/categories-of-user user-id))

(defn add-category [user-id name]
  (try
    (db/add-category! user-id name)
    (println "Category added!")
    (catch Exception e
      (println "Error adding category:" (.getMessage e)))))

(defn edit-category!
  [cat-id name]
  (try
    (db/update-category! cat-id name)
    (println "Category updated!")
    (catch Exception e
      (println "Error updating category:" (.getMessage e)))))

(defn one-letter-different? [input correct]
  (= 1 (count (filter false? (map = input correct)))))

(defn compare-words [word1 word2]
  (let [in   (str/lower-case (str/trim word1))
        corr (str/lower-case (str/trim word2))]

    (if (= in corr)
      {:status :correct
       :message "Correct!"}

      (if (and (= (count in) (count corr))
               (one-letter-different? in corr))
        {:status :almost
         :message (str "Almost! Right answer is: " word2)}

        {:status :wrong
         :message (str "Wrong. Right answer is: " word2)}))))

(defn weight-of-word [row]
  (let [correct (:correct_answers row)
        total   (:total_count row)
        last-attend (.toLocalDate (:last_attend row))
        today (LocalDate/now)
        days-since (.until last-attend today ChronoUnit/DAYS)
        base-weight (if (zero? total)
                        1.0
                        (- 1 (/ correct total)))]
      (+ (* 0.7 base-weight) (* 0.3 (min 1.0 (/ days-since 30.0))))))

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
    {:word-id (:id row)
     :word (:word row)}))


(defn check-translate-word [word-id user-answer]
  (let [row (db/get-word-by-id word-id)
        correct (:translation row)
        result (compare-words user-answer correct)
        correct? (= (:status result) :correct)]
    (db/update-word-stats word-id correct?)
    {:feedback result
     :correct-answer correct}))

(defn generate-sentence-exercise [user-id]
  (let [words (vec (db/list-words-for-ai user-id))
        sentence (ai/generate-sentence words)]
    (when sentence
      {:words words
       :sentence sentence})))

(defn check-translation-exercise [sentence user-input]
  (ai/check-translation sentence user-input))

(defn generate-and-save-ai-words!
  [{:keys [user-id level number language target-language notes]}]
  (let [generated (ai/generate-words
                    {:level level
                     :number number
                     :language language
                     :target-language target-language
                     :notes notes})]

    (mapv (fn [{:keys [word translation]}]
            (let [id (db/add-word! user-id word translation nil)]
              {:id id
               :word word
               :translation translation
               :success 0
               :category_name ""}))
          generated)))