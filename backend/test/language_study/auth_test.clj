(ns language_study.auth_test
  (:require [midje.sweet :refer :all]
            [language_study.auth :as auth]
            [next.jdbc.result-set :as rs]
            [next.jdbc :as jdbc]))

(def db-spec
  {:dbtype "postgresql"
   :dbname "LanguageStudy"
   :host "localhost"
   :port 5432
   :user "postgres"})

(def ds (jdbc/get-datasource db-spec))

(facts "about auth/login"

       (fact "returns user when credentials are correct"
             (auth/login "test" "test") => {:id 3 :username "test" :password "test"}
             (provided
               (jdbc/execute! ds
                              ["SELECT * FROM users WHERE username=? AND password=?" "test" "test"]
                              {:builder-fn rs/as-unqualified-lower-maps}) => [{:id 3 :username "test" :password "test"}]))

       (fact "returns nil when credentials are incorrect"
             (auth/login "test" "wrong") => nil
             (provided
               (jdbc/execute! ds
                              ["SELECT * FROM users WHERE username=? AND password=?" "test" "wrong"]
                              {:builder-fn rs/as-unqualified-lower-maps}) => [])))

