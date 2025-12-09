(ns language_study.database
      (:require [next.jdbc :as jdbc]))

  (def db-spec
       {:dbtype "postgresql"
        :dbname "LanguageStudy"
        :host "localhost"
        :port 5432
        :user "postgres"})

  (def ds (jdbc/get-datasource db-spec))

(defn load-rows-for-success []
                       (map (fn [row]
                              {:word            (:words/word row)
                               :translation     (:words/translation row)
                               :correct_answers (:words/correct_answers row)
                               :total_count     (:words/total_count row)})
                            (jdbc/execute! ds
                                           ["SELECT correct_answers, total_count FROM words"])))

(defn load-all-words []
  (map (fn [row]
         {:word            (:words/word row)
          :translation     (:words/translation row)
          :correct_answers (:words/correct_answers row)
          :total_count     (:words/total_count row)})
       (jdbc/execute! ds
                      ["SELECT word, translation, correct_answers, total_count FROM words"])))


(defn insert-word [word translation]
  (jdbc/execute! ds
                 ["INSERT INTO words (word, translation) VALUES (?, ?)"
                  word translation]))

(defn add-word! [user-id word translation]
  (jdbc/execute! ds
                 ["INSERT INTO words (user_id, word, translation) VALUES (?,?,?)"
                  user-id word translation])
  (println "Word added."))

(defn random-word [user-id]
  (first (jdbc/execute! ds
                        ["SELECT * FROM words WHERE user_id=? ORDER BY RANDOM() LIMIT 1"
                         user-id])))

(defn list-words [user-id]
  (jdbc/execute! ds
                 ["SELECT id, word, translation, created_at FROM words WHERE user_id=? ORDER BY created_at"
                  user-id]))

(defn update-word-stats
  [word-id correct?]
  (jdbc/execute! ds
                 [(str "UPDATE words SET "
                       "total_count = total_count + 1"
                       (when correct? ", correct_answers = correct_answers + 1")
                       " WHERE id = ?")
                  word-id]))