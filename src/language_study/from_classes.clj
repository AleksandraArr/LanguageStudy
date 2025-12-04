(ns language_study.from-classes
  (:require [clojure.string :as str]))

;;Advent of code without functions
(reduce
  (fn [[pos cnt] step]
    [(mod (+ pos step) 100)
     (if (zero? pos) (inc cnt) cnt)])
  [50 0]
  (map (fn [s]
         (Integer/parseInt
           (clojure.string/replace (clojure.string/replace s #"R" "") #"L" "-")))
       (str/split-lines (slurp "direction.txt"))))

(defn read-file [text]
    (str/split-lines (slurp text)))

(defn parsing [seq]
  (Integer/parseInt
    (clojure.string/replace (clojure.string/replace seq #"R" "") #"L" "-")))

(defn rotate [[pos cnt] step]
  [(mod (+ pos step) 100)
   (if (zero? pos) (inc cnt) cnt)])

;;Advent of code with functions
(defn advent-of-code []
  (reduce rotate
          [50 0]
          (map parsing
               (read-file "direction.txt"))))