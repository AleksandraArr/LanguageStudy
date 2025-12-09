(ns language_study.core (:gen-class)
                        (:require [language_study.auth :as auth]
                                  [language_study.database :as db]
                                  [language_study.words :as words]))

(defn main-menu [user]
  (loop []
    (println "\n--- Menu ---")
    (println "1. Add word")
    (println "2. Random word")
    (println "3. Word list")
    (println "4. Exit")
    (print "> ") (flush)
    (case (read-line)
      "1" (do (print "Word: ") (flush)
              (let [w (read-line)]
                (print "Translation: ") (flush)
                (let [t (read-line)]
                  (db/add-word! (:users/id user) w t)))
              (recur))
      "2" (do
            (let [row (db/random-word (:users/id user))
                  word (:words/word row)
                  correct (:words/translation row)]
              (println "Word:" word)
              (print "Your translation: ") (flush)
              (let [user-answer (read-line)]
                (if (words/compare-words user-answer correct)
                  (println "Correct!")
                  (println "Wrong! Correct translation is:" correct))))
            (recur))
      "3" (do (doseq [row (db/list-words (:users/id user))]
                (println (:words/word row) "-" (:words/translation row)))
              (recur))
      "4" (println "Goodbye!")
      (do (println "Unknown option.") (recur)))))

(defn -main []
  (println "1. Register")
  (println "2. Login")
  (print "> ") (flush)
  (case (read-line)
    "1" (do (print "Username: ") (flush)
            (let [u (read-line)]
              (print "Password: ") (flush)
              (let [p (read-line)]
                (auth/register! u p))))
    "2" (do (print "Username: ") (flush)
            (let [u (read-line)]
              (print "Password: ") (flush)
              (let [p (read-line)
                    user (auth/login u p)]
                (if user
                  (main-menu user)
                  (println "Wrong username or password.")))))
    (println "Unknown option.")))