(ns language_study.core (:gen-class)
                        (:require
                                  [language_study.words :as words]))

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

