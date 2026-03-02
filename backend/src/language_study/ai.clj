(ns language_study.ai
  (:require [hato.client :as http]
            [cheshire.core :as json]
            [environ.core :refer [env]]))

(def api-key (env :gemini-api-key))

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
                    "Is this translation fully correct? If yes, say 'Congratulations, you are right!' but in the language of user translation. If not, provide the say 'You are wrong!' 'Correct answert is: 'correct translation and 'Closeness' rate how close it is (0-100). Don't add anything else.")
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

(defn generate-words
  [{:keys [level number language target-language notes]}]
  (let [url "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent"
        prompt (str
                 "You MUST strictly follow these instructions:\n"
                 "Generate exactly " number " vocabulary words.\n"
                 notes "\n\n"
                 "Language: " language ".\n"
                 "Target translation language: " target-language ".\n"
                 "Level: " level " (CEFR level: A1, A2, B1 or B2).\n"
                 "!!! STRICT INSTRUCTIONS !!!\n"
                 "Return ONLY valid JSON array in this format:\n"
                 "[{\"word\": \"example\", \"translation\": \"primer\"}]\n"
                 "Do not add explanations. Do not add markdown.")
        body {:contents [{:parts [{:text prompt}]}]}
        response (http/post url
                            {:headers {"Content-Type" "application/json"
                                       "x-goog-api-key" api-key}
                             :body (json/encode body)
                             :throw-exceptions? false})

        raw-text (-> response
                     :body
                     (json/decode true)
                     (get-in [:candidates 0 :content :parts 0 :text])
                     clojure.string/trim)]
    (try
      (json/decode raw-text true)
      (catch Exception e
        {:error "AI did not return valid JSON"
         :raw raw-text}))))