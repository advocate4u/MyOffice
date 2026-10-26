# F51 acceptance tests: Back navigation

| ID | Scenario | Expected result |
|---|---|---|
| F51-01 | Open a feature screen from Home and press Android Back | Previous screen is restored |
| F51-02 | Navigate Home → Notes → Note editor and press Back | Previous visible screen returns |
| F51-03 | Press Back from root once | Toast asks user to press Back again to close |
| F51-04 | Press Back a second time within two seconds | MyOffice closes |
| F51-05 | Wait more than two seconds after first Back, then press Back | App does not close on that single press |
| F51-06 | Use in-screen navigation controls | Existing feature-specific save/discard prompts remain available |
| F51-07 | Navigate several screens and use Back repeatedly | Screen history unwinds in reverse order without crash |
| F51-08 | Use Back while editing an unsaved document | Feature-specific unsaved-change protection remains effective where implemented |

## Current limitations

- Screen history is in-memory and is cleared when the Activity is recreated or the app process restarts.
- Some feature screens have their own save/discard logic; test these together with the generic screen history.
- Device acceptance tests remain pending.