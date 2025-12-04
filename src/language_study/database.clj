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