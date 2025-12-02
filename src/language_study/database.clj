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
  (jdbc/execute! db-spec
              ["SELECT correct_answers, total_count FROM words"]))
