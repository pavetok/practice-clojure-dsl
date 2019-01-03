(ns practice-clojure-dsl.core-test
  (:require [clojure.test :refer :all]
            [datomic.api :as d]
            [datomock.core :as dm]
            [practice-clojure-dsl.core :refer :all]))

(def ^:dynamic *conn* nil)

(defn make-fixture-conn
  []
  (let [conn (dm/mock-conn)]
    conn))

(defn with-fixture-conn
  [f]
  (binding [*conn* (make-fixture-conn)]
    (f)))

(use-fixtures :each with-fixture-conn)

(deftest conn-exists
  (is (not (nil? *conn*)))
  (is (not (nil? (d/db *conn*)))))

(comment
  (def foo
    [{:rel/eq [:bool :bool]}
     {:rel/eq [:true :true]
      :eq/in [:bool]}
     {:rel/eq [:false :false]
      :eq/in [:bool]}]))
