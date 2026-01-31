(ns language_study.core (:gen-class)
                        (:require [language_study.auth :as auth]
                                  [language_study.database :as db]
                                  [language_study.validators :as validator]
                                  [language_study.words :as words]
                                  [malli.core :as m]
                                  [language_study.api :refer [api-routes]]))

(defn words-menu [user]
  (loop []
    (println "\n--- Words Menu ---")
    (println "4. Export words to Excel")
    (println "5. Import words from file")
    (println "6. Back to main menu")
    (print "> ") (flush)
    (case (read-line)
    "4" (do
          (words/export-xlsx! (:id user))
          (recur))
    "5" (do
          (words/read-from-file "words.txt")
          (recur))
    "6" nil
    (do (println "Unknown option.") (recur)))))


(defn exercises-menu [user]
  (loop []
    (println "\n--- Exercises Menu ---")
    (println "1. Translate the word")
    (println "2. Multiple Choice")
    (println "3. Translate the sentence")
    (println "4. Back to main menu")
    (print "> ") (flush)
    (case (read-line)
      "1" (do (words/translate-word-exercise user)
            (recur))
      "2" (do (words/multiple-choice-exercise user)
            (recur))
      "3" (do (words/translate-sentence-exercise (:id user))
              (recur))
      "4" nil
      (do (println "Unknown option.") (recur)))))

