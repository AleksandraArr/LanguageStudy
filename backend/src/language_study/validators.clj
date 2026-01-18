(ns language_study.validators
  (:require [malli.core :as m]))

(def non-empty-string
  (m/schema [:string {:min 1}]))

(defn valid-option [num-of-options user-input]
  (and (integer? user-input)
       (<= 1 user-input num-of-options)))