(ns language_study.ai
  (:require [hato.client :as http]
            [cheshire.core :as json]))


(def api-key "AIzaSyDc36xQKA3U4_Lw5RjUyFoAvUFgSRV2lq0")


(defn generate-sentence [words]
  (let [url "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent"
        prompt (str "Make only one natural, grammatically correct and short sentence using these three words: "
                    (clojure.string/join ", " words))
        body {:contents
              [{:parts [{:text prompt}]}]}
        response (http/post url
                            {:headers {"Content-Type" "application/json"
                                       "x-goog-api-key" api-key}
                             :body (json/encode body)
                             :throw-exceptions? false})
        sentence (-> response
                    :body
                    (json/decode true)
                    (get-in [:candidates 0 :content :parts 0 :text])
                    clojure.string/trim)]
    sentence))

(defn check-translation [english-sentence user-translation]
  (let [url "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent"
        prompt (str "The sentence is: \"" english-sentence "\".\n"
                    "The user's translation is: \"" user-translation "\".\n"
                    "Is this translation correct? If yes, say 'Congratulations, you are right!' but in the language of user translation. If not, provide the correct translation and rate how close it is (0-100). Don't add anything else.")
        body {:contents [{:parts [{:text prompt}]}]}
        response (http/post url
                            {:headers {"Content-Type" "application/json"
                                       "x-goog-api-key" api-key}
                             :body (json/encode body)
                             :throw-exceptions? false})
        answer (-> response
                     :body
                     (json/decode true)
                     (get-in [:candidates 0 :content :parts 0 :text])
                     clojure.string/trim)]
    answer))