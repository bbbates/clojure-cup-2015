(ns ikea-clojure-cup.client-macros)

(defmacro debounce
  "Wait 'timeout' ms before executing body. If body is executed again within
  the timeout period, then cancels the previous execution and waits timeout
  ms before attempting again."
  [timeout & body]
  `(do
     (defonce timeout-key# (atom nil))
     (let [debounced-f# (fn [] (try ~@body (finally (reset! timeout-key# nil))))]
       (when @timeout-key# (.clearTimeout js/window @timeout-key#))
       (->>
        (.setTimeout js/window debounced-f# ~timeout)
        (reset! timeout-key#)))))
