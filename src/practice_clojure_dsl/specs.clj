(ns practice-clojure-dsl.specs
  (:require [clojure.walk :as w]
            [practice-clojure-dsl.matcho :as m]
            [clojure.spec.alpha :as s]))

(defn- specify-simple
  [val]
  (cond
    (map? val)
    (dissoc val :db/id :val/id)

    (and (coll? val)
         (not (map-entry? val)))
    (vary-meta val assoc :matcho/or true)

    :else val))

(defmulti specify :val/sort)

(defmethod specify :default
  [ty]
  (w/postwalk specify-simple (:val/specs ty)))

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
            :val/sort :val/type.simple
            :val/specs [v1]}]
    (specify t1)
    (meta (:h (first (specify t1)))))

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
