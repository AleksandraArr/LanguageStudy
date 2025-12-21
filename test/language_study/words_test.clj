(ns language_study.words_test
  (:require [midje.sweet :refer :all]
            [language_study.words :as words]
            [language_study.auth :as auth]
            [next.jdbc :as jdbc]))

(fact "compare-words should be case-insensitive"
      (words/compare-words "Hello" "hello") => true
      (words/compare-words "WORLD" "world") => true
      (words/compare-words "Clojure" "Java") => false)

(fact "success_rate calculates correctly"
      (words/success_rate {:correct_answers 1 :total_count 2}) => (double 0.5)
      (words/success_rate {:correct_answers 0 :total_count 0}) => 0
      (words/success_rate {:correct_answers 2 :total_count 2}) => (double 1.0))

(fact "login returns user when credentials are correct"
      (auth/login "aleksa" "ra") => {:username "aleksa" :password "ra"}

      (provided
        (jdbc/execute! anything
                       ["SELECT * FROM users WHERE username=? AND password=?"
                        "aleksa" "ra"])
        => [{:username "aleksa" :password "ra"}]))

