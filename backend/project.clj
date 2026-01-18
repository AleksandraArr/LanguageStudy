(defproject language-study "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"
            }
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [com.github.seancorfield/next.jdbc "1.3.834"]
                 [org.postgresql/postgresql "42.2.10"]
                 [dk.ative/docjure"1.14.0"]
                 [midje "1.10.10"]
                 [uncomplicate/fluokitten "0.10.0"]
                 [uncomplicate/neanderthal "0.60.0"]
                 [criterium "0.4.6"]
                 [metosin/malli "0.10.0"]
                 [net.clojars.wkok/openai-clojure "0.23.0"]
                 [hato "0.9.0"]
                 [cheshire "5.12.0"]
                 [ring "1.11.0"]
                 [ring/ring-json "0.5.1"]
                 [ring-cors "0.1.13"]
                 [compojure "1.7.0"]
                 [ring/ring-jetty-adapter "1.11.0"]]
  :plugins [[lein-midje "3.2.1"]]
  :main ^:skip-aot language_study.api
  :target-path "target/%s"


  :jvm-opts
  ["-Dclojure.compiler.direct-linking=true"
   "--enable-native-access=ALL-UNNAMED"]

  :repl-options {:init-ns matrix-operations.core}
  )
