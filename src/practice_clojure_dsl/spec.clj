(ns practice-clojure-dsl.spec
  (:require [clojure.walk :as w]
            [practice-clojure-dsl.matcho :as m]
            [clojure.spec.alpha :as s])
  (:import (clojure.lang IPersistentMap AMapEntry IPersistentCollection)))

(defprotocol Specify
  (specify-type-val [val]))

(extend-protocol Specify
  IPersistentMap
  (specify-type-val
    [val]
    (dissoc val :db/id :val/id))
  IPersistentCollection
  (specify-type-val
    [vals]
    (vary-meta vals assoc :matcho/or true))
  AMapEntry
  (specify-type-val
    [val]
    val)
  Object
  (specify-type-val
    [val]
    val))

(defmulti specify-1 :val/sort)
(defmethod specify-1 :val/type [ty] (w/postwalk specify-type-val (:type/vals ty)))
(defmethod specify-1 :val/member [m] m)

(defn specify
  [val]
  (specify-1 val))

(comment
  (let [v1 {:val/id "v1"
            :a 0
            :b [0]
            :c :d
            :e #{"s"}
            :f :db.type/ref
            :g {:x :y
                :val/id "v3"}
            :h (sequence [1])}
        v2 {:val/id "v2"}
        t1 {:val/id "t1"
            :val/eq [v2]
            :val/sort :val/type
            :type/vals [v1]}]
    (do
      (specify t1)
      (meta (:h (first (specify t1))))))

  (s/conform (eval (s/or :m map? :n number?)) 0)
  (m/match* "s" (eval (s/or :m map? :n number?)))
  (do))

; 1. для чего будут генерироваться спеки?
;    для значений, но на входе будут отношения
; 2. как интерпретировать?
;    в зависимости от отношения
; 3. что делать с идентификаторами? игнорировать? или они часть значения?
;    пока игнорировать
; 4. как проверять типы? кейворды? лямбды? спека?