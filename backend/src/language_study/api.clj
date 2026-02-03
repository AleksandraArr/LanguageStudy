(ns language_study.api
  (:require
      [compojure.core :refer :all]
      [compojure.route :as route]
      [ring.adapter.jetty :refer [run-jetty]]
      [ring.middleware.cors :refer [wrap-cors]]
      [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
      [ring.middleware.params :refer [wrap-params]]
      [language_study.auth :as auth]
      [language_study.words :as words]))


(defroutes api-routes
           (POST "/api/login" request
             (let [{:keys [username password]} (:body request)
                   user (auth/login username password)]
               (if user
                 {:status 200
                  :body {:success true
                         :user {:id (:id user)
                                :username (:username user)}}}
                 {:status 401
                  :body {:success false
                         :message "Invalid username or password"}})))

           (GET "/api/words" request
             {:status 200
              :body {:success true
                     :words (words/get-words (Integer/parseInt (first (vals (:params request)))) )}})

           (POST "/api/words" request
             (let [{:keys [user-id word translation cat-id]} (:body request)]
               (try
                 (println (first (vals (:params request))))
                 (words/add-word user-id word translation cat-id)
                 {:status 200 :body {:success true}}
                 (catch Exception e
                   {:status 500 :body {:success false :error (.getMessage e)}}))))

           (GET "/api/categories" [user-id]
             (try
               (let [categories (words/categories-of-user (Integer/parseInt user-id))]
                 {:status 200
                  :body {:success true
                         :categories categories}})
               (catch Exception e
                 {:status 500
                  :body {:success false
                         :message (.getMessage e)}})))

           (POST "/api/categories" request
             (let [{:keys [user-id name]} (:body request)]
               (try
                 (words/add-category user-id name)
                 {:status 200
                  :body {:success true}}
                 (catch Exception e
                   {:status 500
                    :body {:success false
                           :message (.getMessage e)}}))))

           (POST "/api/exercise/translate" request
             (let [{:keys [user-id]} (:body request)]
               (if-let [exercise (words/get-translate-word user-id)]
                 {:status 200
                  :body {:success true
                         :data exercise}}
                 {:status 200
                  :body {:success false
                         :message "No words found"}})))


           (POST "/api/exercise/translate/check" request
             (let [{:keys [word-id answer]} (:body request)]
               {:status 200
                :body {:success true
                       :data (words/check-translate-word word-id answer)}}))

           (POST "/api/exercise/multiple-choice"
                 request
             (let [{:keys [user-id]} (:body request)]
               {:status 200
                :body {:success true
                       :data (words/generate-multiple-choice user-id)}}))

           (POST "/api/export-xlsx" request
             (let [{:keys [user-id file-name]} (:body request)]
               (try
                 (words/export-xlsx! user-id file-name)
                 {:status 200
                  :body {:success true}}
                 (catch Exception e
                   {:status 500
                    :body {:success false
                           :message (.getMessage e)}}))))

           (POST "/api/exercise/generate-sentence" request
             (try
               (let [{:keys [user-id]} (:body request)
                     result (words/generate-sentence-exercise user-id)]
                 (if result
                   {:status 200
                    :body {:success true
                           :data result}}))
               (catch Exception e
                 {:status 500
                  :body {:success false
                         :message (.getMessage e)}})))

           (POST "/api/exercise/check-sentence" request
             (try
               (let [{:keys [sentence user-input]} (:body request)
                     feedback (words/check-translation-exercise sentence user-input)]
                 {:status 200
                  :body {:success true
                         :feedback feedback}})
               (catch Exception e
                 {:status 500
                  :body {:success false
                         :message (.getMessage e)}})))

           (route/not-found {:success false :message "Not found"}))



(def app
  (-> api-routes
      (wrap-cors
        :access-control-allow-origin [#"http://localhost:5173"]
        :access-control-allow-methods [:get :post :put :delete :options]
        :access-control-allow-headers ["Content-Type"])
      wrap-params
      (wrap-json-body {:keywords? true :allow-empty-body? true})
      wrap-json-response))

(defn -main []
  (run-jetty app {:port 3000 :join? false}))