(ns language_study.database
      (:require [next.jdbc :as jdbc]
                [next.jdbc.result-set :as rs]))

  (def db-spec
       {:dbtype "postgresql"
        :dbname "LanguageStudy"
        :host "localhost"
        :port 5432
        :user "postgres"})

  (def ds (jdbc/get-datasource db-spec))

(defn load-all-words []
  (jdbc/execute! ds
                 ["SELECT word, translation, correct_answers, total_count FROM words"]
                 {:builder-fn rs/as-unqualified-lower-maps}))

(defn add-word! [user-id word translation cat-id]
  (-> (jdbc/execute! ds
                 ["INSERT INTO words (user_id, word, translation, category_id) VALUES (?,?,?,?) RETURNING id" user-id word translation cat-id]
                 {:builder-fn rs/as-unqualified-lower-maps}) first :id))

(defn delete-word! [word-id]
  (jdbc/execute! ds
                 ["DELETE FROM words WHERE id = ?" word-id]))

(defn update-word!
  [word-id word translation cat-id]
  (jdbc/execute! ds
                 ["UPDATE words
                   SET word = ?,
                       translation = ?,
                       category_id = ?
                   WHERE id = ?"
                  word translation cat-id word-id]))

(defn add-category! [user-id name]
  (-> (jdbc/execute! ds ["INSERT INTO word_categories (user_id, name) VALUES (?, ?) RETURNING id" user-id name]
                     {:builder-fn rs/as-unqualified-lower-maps}) first :id))

(defn update-category!
  [cat-id name]
  (jdbc/execute! ds
                 ["UPDATE word_categories
                   SET name = ?
                   WHERE id = ?"
                  name cat-id]))

(defn categories-of-user [user-id]
  (jdbc/execute! ds
                 ["SELECT id, name FROM word_categories WHERE user_id=? ORDER BY id" user-id]
                 {:builder-fn rs/as-unqualified-lower-maps}))

(defn get-words [user-id]
  (jdbc/execute! ds
                 ["SELECT w.*, c.name AS category_name FROM words w INNER JOIN word_categories c ON w.category_id = c.id WHERE w.user_id=? ORDER BY created_at" user-id]
                 {:builder-fn rs/as-unqualified-lower-maps}))

(defn list-words-for-ai [user-id]
  (->> (jdbc/execute! ds
                      ["SELECT words FROM words WHERE user_id=? ORDER BY created_at" user-id]
                      {:builder-fn rs/as-unqualified-lower-maps})
       shuffle
       (take 3)
       (map :translation)))

(defn get-word-by-id [word-id]
  (first
    (jdbc/execute! ds
                   ["SELECT id, translation
                    FROM words
                    WHERE id = ?" word-id]
                   {:builder-fn rs/as-unqualified-lower-maps})))


(defn update-word-stats
  [word-id correct?]
  (jdbc/execute! ds
                 [(str "UPDATE words SET "
                       "total_count = total_count + 1"
                       (when correct? ", correct_answers = correct_answers + 1")
                       ", last_attend = NOW() "
                       "WHERE id = ?") word-id ]
                 {:builder-fn rs/as-unqualified-lower-maps}))