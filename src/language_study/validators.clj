(ns language_study.validators
  (:require
            [malli.core :as m]))

(def non-empty-string
  (m/schema [:string {:min 1}]))

(defn number-in-options [valid-options user-input]
  (some #(= % user-input) valid-options))