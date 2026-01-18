(ns language_study.words_test
  (:require [midje.sweet :refer :all]
            [language_study.words :as words])
  (:import (java.time LocalDate)))

(fact "compare-words should be case-insensitive"
      (words/compare-words "Hello" "hello") => true
      (words/compare-words "WORLD" "world") => true
      (words/compare-words "Clojure" "Java") => false)

(fact "success_rate calculates correctly"
      (words/success_rate {:correct_answers 1 :total_count 2}) => (double 0.5)
      (words/success_rate {:correct_answers 0 :total_count 0}) => 0
      (words/success_rate {:correct_answers 2 :total_count 2}) => (double 1.0))

(facts "weight-of-word calculates accurately"

       (fact "total_count = 0 should return 1 + 0 days if last_attend today"
             (words/weight-of-word {:correct_answers 0
                                    :total_count 0
                                    :last_attend (LocalDate/now)}) => 1.0)

       (fact "all answers correct should give base-weight 0"
             (words/weight-of-word {:correct_answers 7
                                    :total_count 7
                                    :last_attend (LocalDate/now)}) => 0)

       (fact "some answers wrong, last_attend today"
             (words/weight-of-word {:correct_answers 5
                                    :total_count 7
                                    :last_attend (LocalDate/now)}) => 2/7)

       (fact "some answers wrong, last_attend 1 day ago"
             (words/weight-of-word {:correct_answers 5
                                    :total_count 7
                                    :last_attend (.minusDays (LocalDate/now) 1)}) => 9/7)

       (fact "some answers wrong, last_attend 10 days ago"
             (words/weight-of-word {:correct_answers 5
                                    :total_count 7
                                    :last_attend (.minusDays (LocalDate/now) 10)}) => 72/7)
       (fact "all answers correct, last_attend 100 days ago"
             (words/weight-of-word {:correct_answers 5
                                    :total_count 5
                                    :last_attend (.minusDays (LocalDate/now) 100)}) => 100))
