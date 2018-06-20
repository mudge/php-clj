(defproject php-clj "0.4.1"
  :description "Deserialize PHP into Clojure data structures and back again."
  :url "https://github.com/mudge/php-clj"
  :dependencies [[org.flatland/ordered "1.5.6"]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:dependencies [[midje "1.5.1"]]
                   :plugins [[lein-midje "3.0.0"]
                             [lein-kibit "0.0.8"]]}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5.0 {:dependencies [[org.clojure/clojure "1.5.0"]]}
             :1.5.1 {:dependencies [[org.clojure/clojure "1.5.1"]]}}
  :aliases {"all" ["with-profile" "dev,1.4:dev,1.5.0:dev,1.5.1"]})

