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

;;Advent of code without functions - part 2
(defn advent-of-code-part-2 []
  (reduce
    (fn [[pos cnt] step]
      [(mod (+ pos step) 100)
       (if (pos? step)
         (+ cnt (quot (+ pos step) 100))
         (+ cnt (quot (+ (- 100  pos) (abs step)) 100) (if (zero? pos) -1 0) ))])
    [50 0]
    (map (fn [s]
           (Integer/parseInt
             (clojure.string/replace (clojure.string/replace s #"R" "") #"L" "-")))
         (str/split-lines (slurp "direction.txt")))))

;implemented map with reduce
(defn my-map-with-count [coll]
  (reduce
    (fn [[v n] x]
      [(conj v (dec x))
       (inc n)])
    [[] 0]
    coll))

;;Returns only string with less then 4
(defn small-strings [lst]
  (filter #(<= (count %) 3) lst))

;;Returns the number of characters
(defn count-of-character [lst]
  (reduce + (map count lst)))


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