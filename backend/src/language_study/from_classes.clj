(ns language_study.from-classes
  (:require [uncomplicate.fluokitten.core :as fk])
  (:require [criterium.core :as cc]
            [clojure.string :as str]))

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

;Implemented map with reduce
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

;;Fibonacci with recursion
(defn fibonacci [n]
  ((fn [lst n]
     (if (= (count lst) n)
       lst
       (recur (conj lst (+ (last lst) (nth lst (- (count lst) 2)))) n)))
   [1 1] n))

;;Fibonacci with reduce
((fn [n]
   (reduce (fn [lst _]
             (conj lst
                   (+ (last lst)
                      (nth lst (- (count lst) 2)))))
           [1 1]
           (range (- n 2)))) 3)

(defn dot [x y]
  (fk/foldmap * 0 + x y))

(def my-array (float-array 10))
(def my-array-2 (float-array 10))

(defn report [] (cc/quick-bench (dot my-array my-array-2)))


(defn generate-matrix
  [rows cols]
  (mapv (fn [_] (mapv (fn [_] (+ 1 (rand-int 9))) (range cols)))
        (range rows)))

(defn dot [x y]
  (fk/foldmap * 0 + x y))

(defn transpose [m] (apply map vector m))

(defn dot-product [v1 v2]
  (reduce + (map * v1 v2)))

(defn multiply-matrices [a b]
  (mapv (fn [row]
          (mapv (fn [col]
                  (dot-product row col))
                (transpose b)))
        a))

(def A (generate-matrix 1000 1000))
(def B (generate-matrix 1000 1000))


(defn report [] (time (do (multiply-matrices A B) nil)))

(def mika (atom {:name "Mika" :balance 60}))
(def pera (atom {:name "Pera" :balance 100}))
(defn calculate [user amount operation] (update user :balance operation amount))

(defn transfer [amount from to]
  [(when amount >= from :balance
                (swap! from calculate amount -)
                (swap! to calculate amount +))])
(transfer 10 pera mika)

(defn balance-validator
  [{:keys [balance]}]
  (comp pos? :balance))

(def mika (ref {:name "Mika" :balance 60 } :validator balance-validator))
(def pera (ref {:name "Pera" :balance 100 } :validator balance-validator))

(dosync defn transfer [from to amount]
        (alter from calculate amount -)
        (alter to calculate amount +))
