(ns language_study.auth
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [language_study.database :refer [ds]]))

(defn register! [username password]
  (try
    (jdbc/execute! ds
                   ["INSERT INTO users (username, password) VALUES (?,?)" username password]
                   {:builder-fn rs/as-unqualified-lower-maps})
    nil
    (catch Exception e
      (.getMessage e))))

(defn login [username password]
  (first (jdbc/execute! ds
                        ["SELECT * FROM users WHERE username=? AND password=?" username password]
                        {:builder-fn rs/as-unqualified-lower-maps})))
