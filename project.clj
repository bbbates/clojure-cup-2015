(defproject ikea-clojure-cup "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring-server "0.4.0"]
                 [org.clojars.atroche/reagent "0.6.0-SNAPSHOT" :exclusions [org.clojure/tools.reader]]
                 [reagent-forms "0.5.13"]
                 [reagent-utils "0.1.5"]
                 [alandipert/storage-atom "1.2.4"]
                 [cljsjs/react-bootstrap "0.27.3-0"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [bk/ring-gzip "0.1.1"]
                 [prone "0.8.2"]
                 [metosin/compojure-api "0.24.1"]
                 [hiccup "1.0.5"]
                 [environ "1.0.1"]
                 [org.clojure/clojurescript "1.7.170" :scope "provided"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.1.5" :exclusions [org.clojure/tools.reader]]
                 [cljs-ajax "0.5.1"]
                 [cljs-uuid "0.0.4"]
                 [org.clojure/core.async "0.2.374"]
                 [hickory "0.5.4"]
                 [http-kit "2.1.18"]
                 [org.clojure/data.json "0.2.6"]]

  :plugins [[lein-environ "1.0.1"]
            [lein-cljsbuild "1.1.1"]
            [icm-consulting/lein-less "1.7.6-SNAPSHOT"]
            [lein-ring "0.9.7"]
            [lein-asset-minifier "0.2.2"
             :exclusions [org.clojure/clojure]]]

  :ring {:handler ikea-clojure-cup.handler/app
         :uberwar-name "ikea-clojure-cup.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "ikea-clojure-cup.jar"

  :main ikea-clojure-cup.server

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    [:cljsbuild :builds :app :compiler :output-to]
                                    [:less :clean-path]]

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]

  :less {:source-paths ["src/less/ikea-helper.less"]
         :target-path "resources/public/css"
         :clean-path "resources/public/css/ikea-helper.css"}

  :minify-assets
  {:assets
   {"resources/public/css/ikea-helper.min.css" "resources/public/css/ikea-helper.css"}}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs" "src/cljc"]
                             :compiler {:output-to "target/cljsbuild/public/js/app.js"
                                        :output-dir "target/cljsbuild/public/js/out"
                                        :asset-path   "js/out"
                                        :optimizations :none
                                        :pretty-print  true}}}}

  :profiles {:dev {:repl-options {:init-ns ikea-clojure-cup.repl}

                   :dependencies [[ring/ring-mock "0.3.0"]
                                  [ring/ring-devel "1.4.0"]
                                  [lein-figwheel "0.5.0-2"
                                   :exclusions [org.clojure/core.memoize
                                                ring/ring-core
                                                org.clojure/clojure
                                                org.ow2.asm/asm-all
                                                org.clojure/data.priority-map
                                                org.clojure/tools.reader
                                                org.clojure/clojurescript
                                                org.clojure/core.async
                                                org.clojure/tools.analyzer.jvm]]
                                  [org.clojure/clojurescript "1.7.170"
                                   :exclusions [org.clojure/clojure org.clojure/tools.reader]]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [com.cemerick/piggieback "0.2.1"]

                                  [devcards "0.2.0-8"
                                   :exclusions [org.clojure/tools.reader cljsjs/react]]
                                  [pjstadig/humane-test-output "0.7.0"]
                                  ]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.0-2"
                              :exclusions [org.clojure/core.memoize
                                           ring/ring-core
                                           org.clojure/clojure
                                           org.ow2.asm/asm-all
                                           org.clojure/data.priority-map
                                           org.clojure/tools.reader
                                           org.clojure/clojurescript
                                           org.clojure/core.async
                                           org.clojure/tools.analyzer.jvm]]
                             [org.clojure/clojurescript "1.7.170"]
                             ]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :figwheel {:http-server-root "public"
                              :server-port 3449
                              :nrepl-port 7002
                              :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
                              :css-dirs ["resources/public/css"]
                              :ring-handler ikea-clojure-cup.handler/app}

                   :env {:dev true}

                   :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]
                                              :compiler {:main "ikea-clojure-cup.dev"
                                                         :source-map true}}

                                        :devcards {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                                                   :figwheel {:devcards true}
                                                   :compiler {:main "ikea-clojure-cup.cards"
                                                              :asset-path "js/devcards_out"
                                                              :output-to "target/cljsbuild/public/js/app_devcards.js"
                                                              :output-dir "target/cljsbuild/public/js/devcards_out"
                                                              :source-map-timestamp true}}
                                        }

                               }}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :prep-tasks ["compile" ["cljsbuild" "once"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true
                       :auto-clean false
                       :cljsbuild {:jar true
                                   :builds {:app
                                            {:source-paths ["env/prod/cljs"]
                                             :compiler
                                             {:optimizations :advanced
                                              :pretty-print false}}}}}})
