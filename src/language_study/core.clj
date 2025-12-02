(ns language_study.core (:gen-class)
                        (:require [language_study.words :as ws])
                        (:require [language_study.database :as db]))

(defn -main []
  (println "All rows :" (db/load-rows-for-success))
  (println "Success rate:" (ws/success_rate))
  (println "Success rate:" (ws/success_rate_from_class)))

