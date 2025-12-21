(ns language_study.core (:gen-class)
                        (:require [language_study.auth :as auth]
                                  [language_study.database :as db]
                                  [language_study.words :as words]))

(defn main-menu [user]
  (loop []
    (println "\n--- Menu ---")
    (println "1. Add word")
    (println "2. Add category")
    (println "3. Translate the word")
    (println "4. Word list")
    (println "5. My statistic")
    (println "6. Export words to Excel")
    (println "7. Exit")
    (print "> ") (flush)
    (case (read-line)
      "1" (do
            (print "Word: ") (flush)
            (let [w (read-line)]
              (let [categories (db/categories-of-user (:id user))]
                (println "Your categories:")
                (doseq [{:keys [:id :name]} categories]
                  (println id ":" name))
                (print "Choose category ID: ") (flush)
                (let [cat-id (Integer/parseInt (read-line))]
                  (db/add-word! (:id user) w cat-id))))
            (recur))
      "2" (do (print "Name of category: ") (flush)
              (db/add-category! (:id user) (read-line))
              (recur))
      "3" (do
            (let [row (words/get-random-word (:id user))
                  word (:word row)
                  correct (:translation row)
                  id (:id row)]
              (println "Word:" word)
              (print "Your translation: ") (flush)
              (let [user-answer (read-line)]
                (if (words/compare-words user-answer correct)
                  (do
                    (println "Correct!")
                    (db/update-word-stats id true))
                  (do
                    (println "Wrong! Correct translation is:" correct)
                    (db/update-word-stats id false)))
                ))
            (recur))
      "4" (do (doseq [row (db/list-words (:id user))]
                (println (:word row) "-" (:translation row)))
              (recur))
      "5" (do (doseq [row (words/statistics)]
                (println (:word row) "-" (:translation row) "-" (:success row)))
              (recur))
      "6" (do (words/export-xlsx! (:id user))
              (recur))
      "7" (println "Goodbye!")
      (do (println "Unknown option.") (recur)))))

(defn -main []
  (println "1. Register")
  (println "2. Login")
  (print "> ") (flush)
  (case (read-line)
    "1" (do (print "Username: ") (flush)
            (let [u (read-line)]
              (print "Password: ") (flush)
              (auth/register! u (read-line))))
    "2" (do (print "Username: ") (flush)
            (let [u (read-line)]
              (print "Password: ") (flush)
              (let [user (auth/login u (read-line))]
                (if user
                  (main-menu user)
                  (println "Wrong username or password.")))))
    (println "Unknown option.")))