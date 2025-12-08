(ns language_study.core (:gen-class)
                        (:require [language_study.words :as ws])
                        (:require [language_study.from-classes :as class])
                        (:require [language_study.database :as db]))

(defn -main []
  (println "All rows :" (db/load-all-words))
  (println "Success rate:" (ws/success_rate_average))
  (println "Success rate:" (ws/success_rate_from_class))
  (println "Statistic:" (ws/statistics))
  (println "Enter the name of the document:")
  (let [name-of-file (read-line)]
    (println "Words from document:" (ws/read-words-from-file name-of-file)))
  (let [w (ws/get-random-word)]
    (println "Translate this word:" (:word w))
    (let [user-input (read-line)]
      (if (ws/compare-words user-input (:translation w))
        (println "Correct!")
        (println "Wrong, correct answer is:" (:translation w)))))
  )
