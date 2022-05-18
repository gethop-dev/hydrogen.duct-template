(defproject hydrogen/duct-template "0.5.15"
  :description "Hydrogen duct template"
  :min-lein-version "2.9.8"
  :url "https://github.com/magnetcoop/hydrogen.duct-template"
  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}
  :dependencies [[org.clojure/clojure "1.11.0"]]
  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]]
  :repl-options {:init-ns hydrogen.duct-template}
  :profiles
  {:dev          [:project/dev :profiles/dev]
   :profiles/dev {}
   :project/dev  {:plugins [[jonase/eastwood "1.2.3"]
                            [lein-cljfmt "0.8.0"]]
                  :eastwood {:linters [:all]
                             :exclude-linters [:keyword-typos
                                               :boxed-math
                                               :non-clojure-file
                                               :performance]
                             :debug [:progress :time]}}})
