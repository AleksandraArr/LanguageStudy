(ns language_study.core (:gen-class)
                        (:require [language_study.auth :as auth]
                                  [language_study.database :as db]
                                  [language_study.validators :as validator]
                                  [language_study.words :as words]
                                  [malli.core :as m]))

(defn words-menu [user]
  (loop []
    (println "\n--- Words Menu ---")
    (println "1. Add word")
    (println "2. Add category")
    (println "3. Word list")
    (println "4. Export words to Excel")
    (println "5. Import words from file")
    (println "6. Back to main menu")
    (print "> ") (flush)
    (case (read-line)
      "1" (do
            (print "Word: ") (flush)
            (let [w (read-line)]
              (if (m/validate validator/non-empty-string w)
                (do
                  (print "Translation: ") (flush)
                  (let [t (read-line)]
                    (if (m/validate validator/non-empty-string t)
                      (let [categories (db/categories-of-user (:id user))]
                        (println "Your categories:")
                        (doseq [{:keys [:id :name]} categories]
                          (println id ":" name))
                        (print "Choose category ID: ") (flush)
                        (let [cat-id (Integer/parseInt (read-line))]
                          (db/add-word! (:id user) w t cat-id)))
                      (println "Translation cannot be empty! Word not added."))))
                (println "Word cannot be empty! Word not added.")))(recur))
    "2" (do
          (print "Name of category: ") (flush)
          (db/add-category! (:id user) (read-line))
          (recur))
    "3" (do
          (doseq [row (db/list-words (:id user))]
            (println (:word row) "-" (:translation row)))
          (recur))
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
    (println "3. Back to main menu")
    (print "> ") (flush)
    (case (read-line)
      "1" (do (words/translate-word-exercise user)
            (recur))
      "2" (do (words/multiple-choice-exercise user)
            (recur))
      "3" nil
      (do (println "Unknown option.") (recur)))))


(defn main-menu [user]
  (loop []
    (println "\n--- Main Menu ---")
    (println "1. Words")
    (println "2. Exercises")
    (println "3. My statistics")
    (println "4. Exit")
    (print "> ") (flush)
    (case (read-line)
      "1" (do (words-menu user)
            (recur))
      "2" (do (exercises-menu user)
            (recur))
      "3" (do (doseq [row (words/statistics)]
              (println (:word row) "-" (:translation row) "-" (:success row)))
            (recur))
      "4" (println "Goodbye!")
      (do (println "Unknown option.") (recur)))))

(defn login-loop []
  (loop []
    (print "Username: ") (flush)
    (let [username (read-line)]
      (print "Password: ") (flush)
      (let [user (auth/login username (read-line))]
        (if user
          (main-menu user)
          (do
            (println "Wrong username or password. Please try again.")
            (recur)))))))

(defn register-loop []
  (loop []
    (print "Username: ") (flush)
    (let [username (read-line)]
      (print "Password: ") (flush)
      (let [err (auth/register! username (read-line))]
        (if err
          (do
            (println "Registration error:" err)
            (recur))
          (do
            (println "User registered successfully! Please login.")
            (login-loop)))))))

(defn -main []
  (println "1. Register")
  (println "2. Login")
  (print "> ") (flush)
  (case (read-line)
    "1" (register-loop)
    "2" (login-loop)
    (println "Unknown option.")))