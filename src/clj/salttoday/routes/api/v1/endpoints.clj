(ns salttoday.routes.api.v1.endpoints
  (:require [salttoday.layout :as layout]
            [salttoday.metrics.core :as honeycomb]
            [compojure.core :refer [defroutes GET PUT]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [salttoday.db.core :as db]))

(defn string->number [str]
  "Converts a string to a number, if nil or not a number, returns 0."
  (if (nil? str)
    0
    (let [n (read-string str)]
      (if (number? n) n 0))))

(defroutes endpoints
  (GET "/api/v1/todays-stats" []
    (honeycomb/send-metrics {"api-hit" "todays-stats"})
    (-> (response/ok (db/get-todays-stats))
        (response/header "Content-Type"
                         "application/json")))

  (GET "/api/v1/top-comments" []
    (honeycomb/send-metrics {"page-view" "top-comments"})
    (-> (response/ok {:daily (db/get-top-x-comments 0 3 "score" 1)
                      :weekly (db/get-top-x-comments 0 5 "score" 7)
                      :all-time (db/get-top-x-comments 0 50 "score" -1)})
        (response/header "Content-Type"
                         "application/json")))

  (GET "/api/v1/comments" [offset amount sort-type days search-text user]
    (let [offset-num (string->number offset)
          amount-num (string->number amount)
          days-num (string->number days)]
      (-> (response/ok (db/get-top-x-comments offset-num amount-num sort-type days-num search-text user))
          (response/header "Content-Type"
                           "application/json"))))

  (GET "/api/v1/top-users" []
    (honeycomb/send-metrics {"page-view" "top-users"})
    (-> (response/ok {:users (db/get-top-rated-users 10)})
        (response/header "Content-Type"
                         "application/json"))))