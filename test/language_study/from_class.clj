(ns language-study.from-class
  (:require [midje.sweet :refer :all]
            [language_study.from-classes :as class]))

(fact "fibonacci generates correct sequence"
      (class/fibonacci 3) => [1 1 2]
      (class/fibonacci 5) => [1 1 2 3 5]
      (class/fibonacci 7) => [1 1 2 3 5 8 13])
