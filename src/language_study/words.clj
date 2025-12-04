(ns language_study.words
  (:require [language_study.database :as db]))

(defn success_rate_from_class []
  (let [[total-correct total-count]
        (reduce (fn [[sum-correct sum-total] row]
                  [(+ sum-correct (:correct_answers row))
                   (+ sum-total   (:total_count row))])
                [0 0]
                [{:correct_answers 4 :total_count 5}
                 {:correct_answers 2 :total_count 6}
                 {:correct_answers 6 :total_count 7}
                 {:correct_answers 4 :total_count 8}
                 {:correct_answers 7 :total_count 9}])]
    (/ total-correct total-count)))


(defn success_rate []
  (let [rows (db/load-rows-for-success)]
    (/ (apply + (map :words/correct_answers rows))
       (apply + (map :words/total_count rows)))))

(defn get-random-word []
  (let [ws (db/load-all-words)]
    (nth ws (rand-int (count ws)))))

(defn compare-words [word1 word2]
  (= (.toLowerCase word1) (.toLowerCase word2)))
