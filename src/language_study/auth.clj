(ns language_study.auth
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [language_study.database :refer [ds]]))

(defn register! [username password]
  (try
    (jdbc/execute! ds
                   ["INSERT INTO users (username, password) VALUES (?,?)" username password]
                   {:builder-fn rs/as-unqualified-lower-maps})
    (println "User registered.")
    (catch Exception e
      (println "Error."))))

(defn login [username password]
  (first
    (jdbc/execute! ds
                   ["SELECT * FROM users WHERE username=? AND password=?" username password]
                   {:builder-fn rs/as-unqualified-lower-maps})))
