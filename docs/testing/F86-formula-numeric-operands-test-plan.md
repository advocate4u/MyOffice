# F86 — Formula numeric operands

## Acceptance checks
1. Create a CSV sheet with numeric cells A1=5 and B1=2.
2. Enter `=1+2` and confirm the formula preview reports 3.
3. Enter `=A1+2`, `=A1-B1`, `=A1*B1`, and `=A1/B1`; confirm correct results.
4. Confirm a referenced cell containing nonnumeric text produces a readable error instead of a crash.
5. Confirm division by zero is reported.
6. Confirm aggregate range functions SUM, AVERAGE, MIN, MAX and COUNT still work.
7. Confirm preview remains read-only and saved CSV content is not silently rewritten to formula results.

## Limitations
- This supports a single binary arithmetic operator with numeric constants and/or cell references. It is not a full spreadsheet formula engine; nested expressions, parentheses, negative numeric literals and cross-sheet references are not guaranteed.

## CI/device verification
- CI must compile the debug APK and verify package identity/version.
- Device acceptance should check all arithmetic examples and error cases.
