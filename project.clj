(defproject php-clj "0.0.1-SNAPSHOT"
  :description "Deserialize PHP into Clojure data structures."
  :url "https://github.com/mudge/php-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]
                   :plugins [[lein-midje "3.0.0"]]}})

